package com.savian.cartblitz.config;

import com.savian.cartblitz.service.security.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
@Configuration
@Profile("mysql")
public class SecurityJpaConfig {

    private final JpaUserDetailsService userDetailsService;

    public SecurityJpaConfig(JpaUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/home", "/login", "/register", "/categories", "/webjars/**", "/resources/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/product", "/product/brand/**", "/product/tag/**", "/product/priceRange").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/product/**").permitAll()
                        .requestMatchers("/cart", "/product/add-to-cart/**", "/profile").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.POST, "/order/complete/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/order/id/**", "/orderProduct/**/orderId/**/productId/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers("/addProduct", "/product/**", "/product/api/**", "/orderProduct/**", "/customer/**", "/order/**", "/review/**", "/tag/**", "/warranty/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .permitAll()
                                .loginProcessingUrl("/perform_login")
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .permitAll()
                                .logoutSuccessUrl("/")
                                .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(ex -> ex.accessDeniedPage("/accessDenied"))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

}
