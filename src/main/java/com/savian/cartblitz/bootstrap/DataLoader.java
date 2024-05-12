package com.savian.cartblitz.bootstrap;

import com.savian.cartblitz.model.security.Authority;
import com.savian.cartblitz.model.security.User;
import com.savian.cartblitz.repository.security.AuthorityRepository;
import com.savian.cartblitz.repository.security.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
@Profile("mysql")
public class DataLoader implements CommandLineRunner {

    private AuthorityRepository authorityRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private void loadUserData() {
        if (userRepository.count() == 0){
            Authority adminRole = authorityRepository.save(Authority.builder().role("ROLE_ADMIN").build());
            authorityRepository.save(Authority.builder().role("ROLE_USER").build());

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("12345"))
                    .authority(adminRole)
                    .build();

            userRepository.save(admin);
        }
    }


    @Override
    public void run(String... args) throws Exception {
        loadUserData();
    }
}
