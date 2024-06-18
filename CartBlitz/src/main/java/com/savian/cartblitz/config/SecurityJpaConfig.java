package com.savian.cartblitz.config;

import com.savian.cartblitz.service.security.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

                        .requestMatchers("/", "/home", "/login", "/register", "/categories", "/product**", "/product/**", "/webjars/**", "/resources/**", "/images/**").permitAll()
                        .requestMatchers("/cart", "/order/complete/**", "/product/add-to-cart/**", "/profile", "/review", "/order/**").hasAuthority("ROLE_USER")
                        .requestMatchers("/addProduct", "/orderProduct").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/orderProduct", "/customer", "/order", "/review", "/tags**", "/warranty").hasRole("ADMIN")
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
                .build();
    }

}
