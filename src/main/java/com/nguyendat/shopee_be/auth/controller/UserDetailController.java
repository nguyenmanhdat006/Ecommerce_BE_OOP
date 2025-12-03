package com.nguyendat.shopee_be.auth.controller;

import com.nguyendat.shopee_be.auth.dto.UserDetailsDto;
import com.nguyendat.shopee_be.auth.dto.UserDto;
import com.nguyendat.shopee_be.auth.entities.User;
import com.nguyendat.shopee_be.auth.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserDetailController {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailController.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDetailsDto> getUserProfile(Principal principal){
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        logger.info("User: {}", user);

        if(null == user){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Get the first role (USER or ADMIN)
        String role = user.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority())
                .orElse("USER");

        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .role(role)
                .addressList(user.getAddressList())
                .authorityList(user.getAuthorities().toArray()).build();

        logger.info("UserDetailsDto: {}", userDetailsDto);
        return new ResponseEntity<>(userDetailsDto, HttpStatus.OK);

    }

    /**
     * Get all users (for admin to initiate chat)
     * GET /api/user/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}