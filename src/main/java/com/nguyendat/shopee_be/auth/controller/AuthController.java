package com.nguyendat.shopee_be.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyendat.shopee_be.auth.config.JWTTokenHelper;
import com.nguyendat.shopee_be.auth.dto.LoginRequest;
import com.nguyendat.shopee_be.auth.dto.UserDto;
import com.nguyendat.shopee_be.auth.dto.LoginResponse;
import com.nguyendat.shopee_be.auth.dto.RegistrationRequest;
import com.nguyendat.shopee_be.auth.dto.RegistrationResponse;
import com.nguyendat.shopee_be.auth.entities.User;
import com.nguyendat.shopee_be.auth.services.RegistrationService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JWTTokenHelper jwtTokenHelper;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = UsernamePasswordAuthenticationToken
                    .unauthenticated(loginRequest.getUserName(), loginRequest.getPassword());
            Authentication authenticationResponse = this.authenticationManager.authenticate(authentication);

            if (authenticationResponse.isAuthenticated()) {
                User user = (User) authenticationResponse.getPrincipal();
                if (!user.isEnabled()) {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
                // generate jwt token
                String token = jwtTokenHelper.generateToken(user.getEmail(), user.getAuthorities().iterator().next().getAuthority());

                UserDto userDto = UserDto.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .enabled(user.isEnabled())
                        .avatar(user.getAvatar())
                        .build();
                LoginResponse loginResponse = LoginResponse.builder()
                        .token(token)
                        .user(userDto)
                        .build();

                return new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.OK);
            }

        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest request) {
        RegistrationResponse registrationResponse = registrationService.createUser(request);

        return new ResponseEntity<>(registrationResponse,
                registrationResponse.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> map) {
        String userName = map.get("userName"); // map : lưu trữ dữ liệu dưới dạng key-value
        String code = map.get("code"); // phương thức map.get : lấy value của key đó

        User user = (User) userDetailsService.loadUserByUsername(userName);
        if (null != user && user.getVerificationCode().equals(code)) {
            registrationService.verifyUser(userName);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
