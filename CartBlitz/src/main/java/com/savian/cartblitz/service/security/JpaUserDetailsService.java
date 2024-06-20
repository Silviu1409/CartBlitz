package com.savian.cartblitz.service.security;

import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.model.security.Authority;
import com.savian.cartblitz.repository.CustomerRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("mysql")
public class JpaUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer user;

        Optional<Customer> userOpt= customerRepository.findByUsername(username);
        if (userOpt.isPresent())
            user = userOpt.get();
        else
            throw new UsernameNotFoundException("Username: " + username);

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),user.getEnabled(), user.getAccountNonExpired(),
                user.getCredentialsNonExpired(),user.getAccountNonLocked(),
                getAuthorities(user.getAuthorities()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<Authority> authorities) {
        if (authorities == null){
            return new HashSet<>();
        } else if (authorities.isEmpty()){
            return new HashSet<>();
        }
        else{
            return authorities.stream()
                    .map(Authority::getRole)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }
    }
}
