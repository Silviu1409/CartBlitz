package com.savian.cartblitz.controller;

import com.savian.cartblitz.config.WarrantyValidator;
import com.savian.cartblitz.dto.CustomerDto;
import com.savian.cartblitz.exception.ResourceNotFoundException;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.model.security.Authority;
import com.savian.cartblitz.repository.CustomerRepository;
import com.savian.cartblitz.repository.ProductRepository;
import com.savian.cartblitz.repository.TagRepository;
import com.savian.cartblitz.repository.WarrantyRepository;
import com.savian.cartblitz.repository.security.AuthorityRepository;
import com.savian.cartblitz.service.OrderProductService;
import com.savian.cartblitz.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Controller
@Validated
@io.swagger.v3.oas.annotations.tags.Tag(name = "Main controller", description = "Main endpoint")
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
    @Autowired
    private WarrantyValidator warrantyValidator;

    @RequestMapping(value = {"", "/", "/home"}, produces = MediaType.TEXT_HTML_VALUE)
    @Operation(
            description = "Displays the home page",
            summary = "Home Page",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ModelAndView getHome(){
        return new ModelAndView("main");
    }

    @GetMapping(value = "/accessDenied", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(
            description = "Displays the access denied page",
            summary = "Access Denied Page",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public String accessDeniedPage(){ return "accessDenied"; }

    @GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(
            description = "Displays the login form",
            summary = "Login Form",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public String showLogInForm(){
        return "login";
    }

    @GetMapping(value = "/register", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(
            description = "Displays the registration form",
            summary = "Registration Form",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public String showRegisterForm(Model model) {
        model.addAttribute("customer", new CustomerDto());
        return "register";
    }

    @PostMapping(value = "/register", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(
            description = "Processes the registration form and registers a new user",
            summary = "Register a new user",
            responses = {
                    @ApiResponse(description = "Registration successful", responseCode = "302"),
                    @ApiResponse(description = "Validation error", responseCode = "400"),
                    @ApiResponse(description = "Username already exists", responseCode = "409"),
                    @ApiResponse(description = "Email already exists", responseCode = "409")
            }
    )
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

    @GetMapping(value = "/profile", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(
            description = "Displays the profile page of the logged-in user",
            summary = "View Profile",
            responses = {
                    @ApiResponse(description = "Profile page", responseCode = "200"),
                    @ApiResponse(description = "Redirect to home", responseCode = "302"),
                    @ApiResponse(description = "Access denied", responseCode = "403")
            }
    )
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

    @GetMapping(value = "/cart", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(
            description = "Displays the cart page of the logged-in user",
            summary = "View Cart",
            responses = {
                    @ApiResponse(description = "Cart page", responseCode = "200"),
                    @ApiResponse(description = "Redirect to home", responseCode = "302"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
            }
    )
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
                    Map<OrderProductId, String> errorMessagesMap = new HashMap<>();

                    for (OrderProduct orderProduct : cartOrder.getOrderProducts()) {
                        if (orderProduct.getQuantity() > orderProduct.getProduct().getStockQuantity()) {
                            String errorMessage = "Nu sunt suficiente produse de tipul '" + orderProduct.getProduct().getName() + "' în stoc.";
                            errorMessagesMap.put(orderProduct.getOrderProductId(), errorMessage);
                        }
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

    @GetMapping(value = "/cart/applyCoupon")
    @Operation(
            description = "Apply the current coupon on the shopping cart",
            summary = "Apply cart coupon",
            responses = {
                    @ApiResponse(description = "Apply coupon to cart", responseCode = "200"),
                    @ApiResponse(description = "Redirect to home", responseCode = "302"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
            }
    )
    @CircuitBreaker(name = "applyCouponCart", fallbackMethod = "applyCouponFallback")
    public String applyCouponCart(@RequestHeader(value = "coupon", defaultValue = "coupon") String correlationId, Model model, Principal principal) {
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

                    return "redirect:/";
                }
                else {
                    cartOrder = orderService.applyCoupon(cartOrder.getOrderId(), correlationId);
                }
            }

            model.addAttribute("cartOrder", cartOrder);

            return "redirect:/cart";
        } else {
            return "redirect:/";
        }
    }

    public String applyCouponFallback(String correlationId, Model model, Principal principal) {
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
                    model.addAttribute("errorProductQuantity", "No active cart found.");
                } else {
                    model.addAttribute("errorProductQuantity", "Failed to apply coupon.");
                }
            }

            model.addAttribute("cartOrder", cartOrder);

            return "cart";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping(value = "/addProduct", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(
            description = "Displays the form to add a new product",
            summary = "Add Product Form",
            responses = {
                    @ApiResponse(description = "Add product form", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403")

            }
    )
    public String showProductAdd(Model model) {
        model.addAttribute("product", new Product());
        return "productAdd";
    }

    @PostMapping(value = "/addProduct", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(
            description = "Processes the add product form and saves the new product",
            summary = "Add Product",
            responses = {
                    @ApiResponse(description = "Redirect to home", responseCode = "302"),
                    @ApiResponse(description = "Validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403")
            }
    )
    public String processRegister(@Valid @ModelAttribute("product") Product product,
                                  @RequestParam("images") List<MultipartFile> images,
                                  BindingResult bindingResult,
                                  Model model
    ) {
        log.info("add product form for: {}", product.toString());

        warrantyValidator.validate(product, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("product", product);
            return "productAdd";
        }

        Warranty warranty = product.getWarranty();

        if(warrantyValidator.areAllWarrantyFieldsCompleted(warranty)){
            warranty = warrantyRepository.save(product.getWarranty());

            product.setWarranty(warranty);
        }
        else{
            product.setWarranty(null);
        }

        Set<Tag> newTagsSet = new HashSet<>();

        for (Tag tag : product.getTags()) {
            tag.setName(tag.getName().toUpperCase());

            if(tag.getName().isEmpty() || tag.getName().isBlank()){
                continue;
            }

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

        if(newTagsSet.isEmpty()){
            product.setTags(null);
        }

        Product savedProduct = productRepository.save(product);

        log.info("added product successfully: {}", savedProduct);

        int idx = 1;

        if(images != null) {
            for (MultipartFile image: images) {
                if (image.isEmpty()) {
                    continue;
                }

                try {
                    byte[] imageBytes = image.getBytes();

                    ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                    BufferedImage originalImage = ImageIO.read(bais);

                    BufferedImage resizedImage = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = resizedImage.createGraphics();
                    g2d.drawImage(originalImage.getScaledInstance(800, 800, Image.SCALE_SMOOTH), 0, 0, null);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(resizedImage, "jpg", baos);
                    byte[] resizedImageBytes = baos.toByteArray();

                    Long productId = savedProduct.getProductId();

                    String productDir = "src/main/resources/static/images/products/" + product.getCategory() + "/";
                    String imageName = productId + "_" + idx + ".jpg";
                    Path imagePath = Paths.get(productDir, imageName);
                    Files.write(imagePath, resizedImageBytes);

                    idx++;
                } catch (IOException e) {
                    log.error(e.toString());

                    return "redirect:/";
                }
            }

            log.info("added product images successfully: {}", savedProduct);
        }

        return "redirect:/";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handlerNotFoundException(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("notFoundException");
        return modelAndView;
    }
}
