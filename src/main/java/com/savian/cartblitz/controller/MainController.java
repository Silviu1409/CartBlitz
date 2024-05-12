package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.CustomerDto;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.model.Order;
import com.savian.cartblitz.model.OrderStatusEnum;
import com.savian.cartblitz.model.security.Authority;
import com.savian.cartblitz.repository.CustomerRepository;
import com.savian.cartblitz.repository.security.AuthorityRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class MainController {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @RequestMapping({"","/","/home"})
    public ModelAndView getHome(){
        return new ModelAndView("main");
    }

    @GetMapping("/login")
    public String showLogInForm(){
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("customer", new CustomerDto());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute CustomerDto customer,
                               BindingResult bindingResult,
                               Model model
    ){

        if (bindingResult.hasErrors()){
            model.addAttribute("customer", customer);
            return "register";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);

        Authority userRole = authorityRepository.findByRole("ROLE_USER");
        Customer user = Customer.builder()
                .username(customer.getUsername())
                .password(encodedPassword)
                .email(customer.getEmail())
                .fullName(customer.getFullName())
                .authority(userRole)
                .build();
        customerRepository.save(user);

        return "redirect:/login" ;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        String username = principal.getName();
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(username);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            List<Order> completedOrders = customer.getOrders().stream()
                    .filter(order -> order.getStatus() == OrderStatusEnum.COMPLETED)
                    .collect(Collectors.toList());
            model.addAttribute("customer", customer);
            model.addAttribute("completedOrders", completedOrders);
            return "profile";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        String username = principal.getName();
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(username);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            Optional<Order> cartOrderOptional = customer.getOrders().stream()
                    .filter(order -> order.getStatus() == OrderStatusEnum.CART)
                    .findFirst();

            Order cartOrder = cartOrderOptional.orElse(null);
            model.addAttribute("cartOrder", cartOrder);
            return "cart";
        } else {
            return "redirect:/";
        }
    }
}
