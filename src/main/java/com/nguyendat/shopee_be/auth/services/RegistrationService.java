package com.nguyendat.shopee_be.auth.services;

import com.nguyendat.shopee_be.auth.dto.RegistrationRequest;
import com.nguyendat.shopee_be.auth.dto.RegistrationResponse;
import com.nguyendat.shopee_be.auth.entities.Authority;
import com.nguyendat.shopee_be.auth.entities.User;
import com.nguyendat.shopee_be.auth.helper.VerificationCodeGenerator;
import com.nguyendat.shopee_be.auth.repositories.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;
import java.util.List;

@Service
public class  RegistrationService {

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public RegistrationResponse createUser(RegistrationRequest request) {

        User existing = userDetailRepository.findByEmail(request.getEmail());

        if(null != existing){
            return  RegistrationResponse.builder()
                    .code(400)
                    .message("Email already exist!")
                    .build();
        }

        try{

            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setEnabled(false);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setProvider("manual");

            String code = VerificationCodeGenerator.generateCode();

            user.setVerificationCode(code);

            // Thay toàn bộ đoạn cũ bằng đúng 1 dòng này:
            ((List<Authority>) (List<?>) user.getAuthorities()).addAll(authorityService.getUserAuthority());
            userDetailRepository.save(user); 

            //call method to send email
            emailService.sendMail(user);


            return RegistrationResponse.builder()
                    .code(200)
                    .message("User created!")
                    .build();


        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ServerErrorException(e.getMessage(),e.getCause());
        }
    }

    public void verifyUser(String userName) {
        User user= userDetailRepository.findByEmail(userName);
        user.setEnabled(true);
        userDetailRepository.save(user);
    }
}