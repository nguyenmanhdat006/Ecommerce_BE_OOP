package com.nguyendat.shopee_be.auth.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nguyendat.shopee_be.entities.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "AUTH_USER_DETAILS")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    private String firstName;
    private String lastName;

    @JsonIgnore
    private String password;

    private Date createdOn;
    private Date updatedOn;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;
    private String provider;
    private String verificationCode;
    private boolean enabled = false;

    @Column(nullable = true)
    private String avatar;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        @JoinTable(
            name = "auth_user_authority",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), 
            inverseJoinColumns = @JoinColumn(name = "authorities_id", referencedColumnName = "id"))
    private List<Authority> authorities = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Address> addressList;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Cart> carts;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<PurchaseHistory> purchaseHistories;

    @OneToMany(mappedBy = "changedBy", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ProductStatus> productStatusChanges;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}