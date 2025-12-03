package com.nguyendat.shopee_be.auth.services;

import com.nguyendat.shopee_be.auth.entities.User;
import com.nguyendat.shopee_be.auth.repositories.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private AuthorityService authorityService;

    // Lấy user từ DB theo email
    public User getUser(String email) {
        return userDetailRepository.findByEmail(email);
    }

    // Tạo user mới từ thông tin OAuth2
    public User createUser(OAuth2User oAuth2User, String provider) {
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String email = oAuth2User.getAttribute("email");

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .provider(provider)
                .enabled(true)
                .authorities(authorityService.getUserAuthority())
                .build();

        return userDetailRepository.save(user);
    }

    // Đồng bộ user khi OAuth2 login
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService()
                .loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        User user = getUser(email);

        if (user == null) {
            user = createUser(oAuth2User, userRequest.getClientRegistration().getRegistrationId());
        }

        return oAuth2User;
    }
}
