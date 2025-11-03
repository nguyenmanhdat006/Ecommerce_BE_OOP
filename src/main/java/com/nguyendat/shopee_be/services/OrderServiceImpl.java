package com.nguyendat.shopee_be.services;
import com.nguyendat.shopee_be.auth.repositories.UserDetailRepository;
// import com.nguyendat.shopee_be.dto.OrderItemRequest;
import com.nguyendat.shopee_be.dto.OrderRequest;
import com.nguyendat.shopee_be.entities.*;
import com.nguyendat.shopee_be.exceptions.ResourceNotFoundEx;
import com.nguyendat.shopee_be.repositories.*;
// import com.nguyendat.shopee_be.ultil.VnpayUtil;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.nguyendat.shopee_be.auth.entities.User;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class OrderServiceImpl implements OrderService {

    @Value("${VNPAY_URL:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpUrl;

    @Value("${VNPAY_TMN_CODE:}")
    private String vnpTmnCode;

    @Value("${VNPAY_HASH_SECRET:}")
    private String vnpHashSecret;

    @Value("${VNPAY_RETURN_URL_BASE:http://localhost:5173}")
    private String vnpReturnUrlBase;


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserDetailRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundEx("Order not found with id: " + id));
    }

    @Override
    public Order create(OrderRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundEx("User not found with id: " + request.getCustomerId()));

        Order order = new Order();
        order.setOrderNumber(request.getOrderNumber());
        order.setOrderDate(new Date());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(request.getStatus());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingAddress(request.getShippingAddress());
        order.setNotes(request.getNotes());
        order.setCustomer(customer);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = request.getOrderItems().stream().map(itemReq -> {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setTotalPrice(itemReq.getTotalPrice());

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundEx("Product not found with id: " + itemReq.getProductId()));

            ProductVariant variant = productVariantRepository.findById(itemReq.getProductVariantId())
                    .orElseThrow(() -> new ResourceNotFoundEx("Product variant not found with id: " + itemReq.getProductVariantId()));

            item.setProduct(product);
            item.setProductVariant(variant);

            return orderItemRepository.save(item);
        }).collect(Collectors.toList());

        savedOrder.setOrderItems(orderItems);
        return orderRepository.save(savedOrder);
    }

    // @Override
    // public Order update(UUID id, OrderRequest request) {
    //     Order existing = orderRepository.findById(id)
    //             .orElseThrow(() -> new ResourceNotFoundEx("Order not found with id: " + id));

    //     existing.setStatus(request.getStatus());
    //     existing.setNotes(request.getNotes());
    //     existing.setPaymentMethod(request.getPaymentMethod());
    //     return orderRepository.save(existing);
    // }

    @Override
    public void deleteById(UUID id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order update(UUID id, OrderRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public String createVnpayUrl(UUID orderId, BigDecimal amount) {
        // Lấy thông tin đơn hàng
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundEx("Order not found"));

    // Thông tin cố định VNPAY (lấy từ cấu hình / env)
    String vnp_ReturnUrl = vnpReturnUrlBase + "/vnpay-done/" + orderId;

        // Các tham số gửi sang VNPAY
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
    vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", amount.multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", orderId.toString());
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnpParams.put("vnp_IpAddr", "127.0.0.1");

        try {
            // Tạo query string sort theo key + encode UTF-8
            String query = vnpParams.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

            // Tạo chữ ký HMAC-SHA512 bằng thư viện chuẩn Java
            Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(vnpHashSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmacSHA512.init(secretKeySpec);
            byte[] hashBytes = hmacSHA512.doFinal(query.getBytes(StandardCharsets.UTF_8));

            // Chuyển sang hex lowercase (VNPAY yêu cầu)
            StringBuilder hexHash = new StringBuilder();
            for (byte b : hashBytes) {
                hexHash.append(String.format("%02x", b & 0xff));
            }
            String secureHash = hexHash.toString();

            // Trả về URL đầy đủ
            return vnpUrl + "?" + query + "&vnp_SecureHash=" + secureHash;

        } catch (Exception e) {
            throw new RuntimeException("Error generating VNPAY secure hash", e);
        }
    }



    @Override
    public boolean processVnpayReturned(Map<String,String> params) {
    String orderId = params.get("vnp_TxnRef");
    String rspCode = params.get("vnp_ResponseCode");
    Optional<Order> orderOpt = orderRepository.findById(UUID.fromString(orderId));
    if(orderOpt.isPresent() && "00".equals(rspCode)) {
        Order order = orderOpt.get();
        order.setStatus("PAID");
        orderRepository.save(order);
        return true;
    }
    return false;
    }

    @Override
    public Order updateStatus(UUID orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundEx("Order not found"));

        if (!isValidTransition(order.getStatus(), newStatus)) {
            throw new IllegalArgumentException(
                    "Không thể chuyển từ " + order.getStatus() + " sang " + newStatus
            );
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    private boolean isValidTransition(String currentStatus, String newStatus) {
        return switch (currentStatus) {
            case "PENDING" -> List.of("SHIPPING", "CANCELED").contains(newStatus);
            case "SHIPPING" -> List.of("WAIT_DELIVER", "CANCELED").contains(newStatus);
            case "WAIT_DELIVER" -> List.of("PAID", "REFUND").contains(newStatus);
            case "PAID" -> List.of("REFUND").contains(newStatus);
            default -> false;
        };
    }
}