package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.CustomerDto;
import com.savian.cartblitz.dto.OrderProductDto;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.model.security.Authority;
import com.savian.cartblitz.repository.CustomerRepository;
import com.savian.cartblitz.repository.ProductRepository;
import com.savian.cartblitz.repository.TagRepository;
import com.savian.cartblitz.repository.WarrantyRepository;
import com.savian.cartblitz.repository.security.AuthorityRepository;
import com.savian.cartblitz.service.OrderProductService;
import com.savian.cartblitz.service.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class MainController {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderProductService orderProductService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private WarrantyRepository warrantyRepository;
    @Autowired
    private TagRepository tagRepository;

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
    public String processRegister(@Valid @ModelAttribute("customer") CustomerDto customer,
                               BindingResult bindingResult,
                               Model model
    ){
        log.info("register form for: {}", customer.getUsername());

        if (bindingResult.hasErrors()){
            model.addAttribute("customer", customer);
            return "register";
        }

        Optional<Customer> existingCustomer = customerRepository.findByUsername(customer.getUsername());
        if (existingCustomer.isPresent()) {
            model.addAttribute("customer", customer);
            model.addAttribute("registrationError", "Alege alt username");

            log.warn("User name already exists.");

            return "register";
        }

        existingCustomer = customerRepository.findByEmail(customer.getEmail());
        if (existingCustomer.isPresent()) {
            model.addAttribute("customer", customer);
            model.addAttribute("registrationError", "Ai deja un cont creat cu această adresă de email");

            log.warn("Email already exists.");

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

        log.info("registration successful for user: {}", customer.getUsername());

        return "redirect:/login";
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
    public String viewCart(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(username);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            Optional<Order> cartOrderOptional = customer.getOrders().stream()
                    .filter(order -> order.getStatus() == OrderStatusEnum.CART)
                    .findFirst();

            Order cartOrder = cartOrderOptional.orElse(null);

            if(cartOrder != null){
                if(cartOrder.getOrderProducts().isEmpty()){
                    orderService.removeOrderById(cartOrder.getOrderId());

                    cartOrder = null;
                }
                else {
                    BigDecimal actGrandTotal = cartOrder.getTotalAmount(), newGrandTotal = BigDecimal.ZERO;
                    Map<OrderProductId, String> errorMessagesMap = new HashMap<>();

                    for (OrderProduct orderProduct : cartOrder.getOrderProducts()) {
                        BigDecimal productPrice = productRepository.getReferenceById(orderProduct.getProduct().getProductId()).getPrice();
                        Integer productQuantity = orderProduct.getQuantity();
                        BigDecimal totalPrice = BigDecimal.valueOf(productQuantity).multiply(productPrice);

                        if (!Objects.equals(orderProduct.getPrice(), totalPrice)) {
                            OrderProductDto orderProductDto = new OrderProductDto(cartOrder.getOrderId(), orderProduct.getProduct().getProductId(), orderProduct.getQuantity(), totalPrice);
                            orderProductService.updateOrderProduct(cartOrder.getOrderId(), orderProduct.getProduct().getProductId(), orderProductDto);

                            orderProduct.setPrice(totalPrice);
                        }

                        newGrandTotal = newGrandTotal.add(totalPrice);

                        if (orderProduct.getQuantity() > orderProduct.getProduct().getStockQuantity()) {
                            String errorMessage = "Nu sunt suficiente produse de tipul '" + orderProduct.getProduct().getName() + "' în stoc.";
                            errorMessagesMap.put(orderProduct.getOrderProductId(), errorMessage);
                        }
                    }

                    if (!Objects.equals(actGrandTotal, newGrandTotal)) {
                        orderService.updateTotalAmount(cartOrder.getOrderId(), newGrandTotal);
                    }

                    model.addAttribute("errorMessagesMap", errorMessagesMap);
                }
            }

            model.addAttribute("cartOrder", cartOrder);

            if (redirectAttributes.getFlashAttributes().containsKey("errorProductQuantity")) {
                String errorProductQuantity = (String) redirectAttributes.getFlashAttributes().get("errorProductQuantity");

                model.addAttribute("errorProductQuantity", errorProductQuantity);
            }

            return "cart";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/addProduct")
    public String showProductAdd(Model model) {
        model.addAttribute("product", new Product());
        return "productAdd";
    }

    @PostMapping("/addProduct")
    public String processRegister(@Valid @ModelAttribute("product") Product product,
                                  BindingResult bindingResult,
                                  Model model
    ) {
        log.info("add product form for: {}", product.toString());

        if (bindingResult.hasErrors()) {
            model.addAttribute("product", product);
            return "productAdd";
        }

        Warranty warranty = product.getWarranty();

        if(warranty != null){
            warranty = warrantyRepository.save(product.getWarranty());

            product.setWarranty(warranty);
        }

        Set<Tag> newTagsSet = new HashSet<>();

        for (Tag tag : product.getTags()) {
            tag.setName(tag.getName().toUpperCase());
            Optional<Tag> existingTagOptional = tagRepository.findByName(tag.getName());
            if (existingTagOptional.isPresent()) {
                Tag existingTag = existingTagOptional.get();
                newTagsSet.add(existingTag);
            } else {
                Tag savedTag = tagRepository.save(tag);
                newTagsSet.add(savedTag);
            }
        }

        product.setTags(newTagsSet.stream().toList());

        Product savedProduct = productRepository.save(product);

        log.info("added product successfully: {}", savedProduct);

        return "redirect:/";
    }
}
