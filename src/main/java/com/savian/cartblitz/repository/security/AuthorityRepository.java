package com.savian.cartblitz.repository.security;

import com.savian.cartblitz.model.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    Authority findByRole(String role);
}
