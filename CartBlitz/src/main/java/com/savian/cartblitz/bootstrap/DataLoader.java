package com.savian.cartblitz.bootstrap;

import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.model.security.Authority;
import com.savian.cartblitz.repository.CustomerRepository;
import com.savian.cartblitz.repository.security.AuthorityRepository;
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
    private CustomerRepository customerRepository;
    private PasswordEncoder passwordEncoder;

    private void loadUserData() {
        if (customerRepository.count() == 0){
            Authority adminRole = authorityRepository.save(Authority.builder().role("ROLE_ADMIN").build());
            authorityRepository.save(Authority.builder().role("ROLE_USER").build());

            Customer admin = Customer.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("12345"))
                    .email("admin")
                    .fullName("admin")
                    .authority(adminRole)
                    .build();

            customerRepository.save(admin);
        }
    }


    @Override
    public void run(String... args) throws Exception {
        loadUserData();
    }
}
