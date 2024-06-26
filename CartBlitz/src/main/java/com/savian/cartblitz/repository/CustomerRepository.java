package com.savian.cartblitz.repository;

import com.savian.cartblitz.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);
    Optional<Customer> findByEmail(String email);
    List<Customer> findAllByOrderByFullNameAsc();
    List<Customer> findAllByOrderByFullNameDesc();
}
