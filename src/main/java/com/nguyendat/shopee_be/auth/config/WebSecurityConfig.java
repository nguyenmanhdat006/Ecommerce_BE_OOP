package com.nguyendat.shopee_be.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTTokenHelper jwtTokenHelper;

    private static final String[] publicApis= {
            "/api/auth/**",
            "/api/files/**",
            "/files/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize)-> authorize
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/products","/api/category").permitAll()
                        .requestMatchers("/oauth2/success").permitAll()
                        .requestMatchers("/api/upload/**", "/uploads/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")
                        .successHandler((request, response, authentication) -> {
                            // Sinh JWT token cho user
                            String token = jwtTokenHelper.generateToken(authentication.getName());

                            // Redirect về React app kèm token
                            response.sendRedirect("http://localhost:5173/v1/oauth2/callback?token=" + token);
                        })
                )                //.exceptionHandling((exception)-> exception.authenticationEntryPoint(new RESTAuthenticationEntryPoint()))
                .addFilterBefore(new JWTAuthenticationFilter(jwtTokenHelper,userDetailsService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    //bỏ qua không cần để req auth đi qua security chain
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().requestMatchers(publicApis);
    }


    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider daoAuthenticationProvider= new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(daoAuthenticationProvider);

    }
    //PasswordEncoder là interface do Spring Security cung cấp, định nghĩa các phương thức dùng để mã hóa và kiểm tra mật khẩu:
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); //Tạo bộ mã hóa đa năng, mặc định là BCrypt
    }
}
