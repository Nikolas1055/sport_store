package com.example.sport_store.controller;

import com.example.sport_store.entity.*;
import com.example.sport_store.component.Cart;
import com.example.sport_store.repository.*;
import com.example.sport_store.service.CustomValidationService;
import com.example.sport_store.service.SaveUpdateCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private static final String UPLOAD_FOLDER = "upload/customer_image/";
    final Cart cart;
    final OrderRepository orderRepository;
    final CustomerRepository customerRepository;
    final CategoryRepository categoryRepository;
    final RoleRepository roleRepository;
    final AssignedRoleRepository assignedRoleRepository;
    final ProductRepository productRepository;
    final ProductAttributeRepository productAttributeRepository;
    final ManufacturerRepository manufacturerRepository;
    final OrderPositionRepository orderPositionRepository;
    final OrderStatusRepository orderStatusRepository;
    final SaveUpdateCustomerService saveUpdateCustomerService;
    final
    CustomValidationService customValidationService;

    @Autowired
    public MainController(RoleRepository roleRepository,
                          Cart cart,
                          OrderRepository orderRepository,
                          SaveUpdateCustomerService saveUpdateCustomerService,
                          CustomerRepository customerRepository,
                          CategoryRepository categoryRepository,
                          AssignedRoleRepository assignedRoleRepository,
                          ProductRepository productRepository,
                          ProductAttributeRepository productAttributeRepository,
                          ManufacturerRepository manufacturerRepository,
                          OrderPositionRepository orderPositionRepository,
                          OrderStatusRepository orderStatusRepository,
                          CustomValidationService customValidationService) {
        this.roleRepository = roleRepository;
        this.cart = cart;
        this.orderRepository = orderRepository;
        this.saveUpdateCustomerService = saveUpdateCustomerService;
        this.customerRepository = customerRepository;
        this.categoryRepository = categoryRepository;
        this.assignedRoleRepository = assignedRoleRepository;
        this.productRepository = productRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.orderPositionRepository = orderPositionRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.customValidationService = customValidationService;
    }

    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        model.addAttribute("image_path", "/" + UPLOAD_FOLDER);
        model.addAttribute("catalog", categoryRepository.findAll());
        model.addAttribute("cart", cart.getProductAttributeList());
        model.addAttribute("cartSize", cart.getProductCount());
        model.addAttribute("quantityError", cart.getQuantityError());
        String user;
        if (principal != null) {
            Customer customer = customerRepository.findUserByLogin(principal.getName());
            user = customer.getSurname() + " " + customer.getName();
        } else {
            user = "Гость";
        }
        model.addAttribute("user", user);
    }

    @GetMapping("/profile")
    public String profilePageView(Model model, Principal principal) {
        Customer customer = customerRepository.findUserByLogin(principal.getName());
        model.addAttribute("userData", customer);
        model.addAttribute("customerOrders", orderRepository.findOrdersByCustomer(customer));
        return "customer/profile";
    }

    @GetMapping("/register")
    public String registerNewCustomerPageView(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/saveCustomer")
    public String saveCustomer(@Valid Customer customer,
                               BindingResult result,
                               MultipartFile image,
                               @RequestParam String password,
                               @RequestParam String email,
                               @RequestParam String phone,
                               @RequestParam String login) {
        if (customValidationService.checkCustomerUniqueFields(result, login, phone, email, null).hasErrors()) {
            return "register";
        }
        saveUpdateCustomerService.saveNewCustomer(image, customer, password, null);
        return "redirect:/login";
    }

    @GetMapping("/showProducts")
    public String showProducts(Long id, Model model) {
        Category category = categoryRepository.getCategoryById(id);
        model.addAttribute("productsList",
                productRepository.getProductsByCategory(category));
        model.addAttribute("chosenCategoryName", category.getName());
        return "index";
    }

    @GetMapping(value = {"/", "/index"})
    public String welcomePageView(Model model) {
        List<Manufacturer> manufacturers = manufacturerRepository.findAll();
        Collections.shuffle(manufacturers);
        model.addAttribute("popular", productRepository.findTop5ByOrderByRatingDesc());
        model.addAttribute("brands", manufacturers.stream().limit(7).collect(Collectors.toList()));
        return "index";
    }

    @GetMapping("/productInfo")
    public String productInfo(Model model, Long id) {
        Product product = productRepository.getProductById(id);
        model.addAttribute("product", product);
        List<ProductAttribute> productAttributes = productAttributeRepository.getProductAttributesByProduct(product);
        Map<Color, List<ProductAttribute>> colorListMap = new HashMap<>();
        if (!productAttributes.isEmpty()) {
            for (ProductAttribute productAttribute : productAttributes) {
                if (!colorListMap.containsKey(productAttribute.getColor())) {
                    colorListMap.put(productAttribute.getColor(), new ArrayList<>());
                }
                colorListMap.get(productAttribute.getColor()).add(productAttribute);
            }
        }
        model.addAttribute("attributes", colorListMap);
        return "index";
    }

    @GetMapping("/addProductAttrToCart")
    public String addProductAttrToCart(@RequestParam String attr, HttpServletRequest request) {
        cart.addProductAttribute(Long.parseLong(attr));
        cart.setQuantityError(checkQuantity());
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/cart")
    public String showCart(Model model) {
        cart.setQuantityError(checkQuantity());
        Map<ProductAttribute, Integer> attributeMap = new HashMap<>();
        if (!cart.getProductAttributeList().isEmpty()) {
            for (Map.Entry<Long, Integer> cartPosition : cart.getProductAttributeList().entrySet()) {
                attributeMap.put(productAttributeRepository.getById(cartPosition.getKey()), cartPosition.getValue());
            }
        }
        model.addAttribute("cartList", attributeMap);
        model.addAttribute("sum", getSum());
        return "customer/cart";
    }

    @GetMapping("/deleteCartPosition")
    public String deleteCartPosition(Long id, HttpServletRequest request) {
        cart.deleteProductAttribute(id);
        cart.setQuantityError(checkQuantity());
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/makeOrder")
    public String makeOrder(HttpServletRequest request, Principal principal) {
        cart.setQuantityError(checkQuantity());
        if (cart.getQuantityError().isEmpty()) {
            Order order = new Order();
            if (principal != null) {
                order.setUser(customerRepository.findUserByLogin(principal.getName()));
            }
            order.setOrderCreateDate(new Timestamp(System.currentTimeMillis()));
            order.setOrderSum(getSum());
            order.setOrderStatus(orderStatusRepository.getOrderStatusById(1L));
            orderRepository.save(order);
            for (Map.Entry<Long, Integer> position : cart.getProductAttributeList().entrySet()) {
                OrderPosition orderPosition = new OrderPosition();
                orderPosition.setOrder(order);
                orderPosition.setQuantity(position.getValue());
                ProductAttribute productAttribute = productAttributeRepository.getById(position.getKey());
                productAttribute.setQuantity(productAttribute.getQuantity() - position.getValue());
                productAttributeRepository.save(productAttribute);
                orderPosition.setProductAttribute(productAttribute);
                orderPositionRepository.save(orderPosition);
            }
            cart.clear();
            cart.setQuantityError("");
        }
        return "redirect:" + request.getHeader("Referer");
    }

    private String checkQuantity() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Long, Integer> position : cart.getProductAttributeList().entrySet()) {
            ProductAttribute productAttribute = productAttributeRepository.getById(position.getKey());
            if (position.getValue() > productAttribute.getQuantity()) {
                result
                        .append(productAttribute.getProduct().getName())
                        .append(", цвет ")
                        .append(productAttribute.getColor().getName())
                        .append(", размер ")
                        .append(productAttribute.getSize().getName())
                        .append(", в наличии ")
                        .append(productAttribute.getQuantity())
                        .append(", в заказе ")
                        .append(position.getValue())
                        .append("<br>");
            }
        }
        return result.toString();
    }

    private double getSum() {
        double sum = 0;
        for (Map.Entry<Long, Integer> position : cart.getProductAttributeList().entrySet()) {
            sum += productAttributeRepository.getById(position.getKey()).getProduct().getPrice() * position.getValue();
        }
        return sum;
    }

    @GetMapping("/clearCart")
    public String clearCart(HttpServletRequest request) {
        cart.clear();
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/editData")
    public String editProfileData(Model model, Long id) {
        model.addAttribute("customer", customerRepository.getCustomerById(id));
        return "customer/editProfile";
    }

    @PostMapping("/editData")
    public String saveNewProfileData(@Valid Customer customer,
                                     MultipartFile image,
                                     BindingResult result,
                                     @RequestParam Long id,
                                     @RequestParam String name,
                                     @RequestParam String surname,
                                     @RequestParam String phone,
                                     @RequestParam String email,
                                     @RequestParam String country,
                                     @RequestParam String city,
                                     @RequestParam String login) {
        if (customValidationService.checkCustomerUniqueFields(result, login, phone, email, id).hasErrors()) {
            return "customer/editProfile";
        }
        saveUpdateCustomerService
                .updateExistCustomer(customer, image, name, surname, phone, email, country, city, login, null);
        return "redirect:/profile";
    }

    @GetMapping("/findCustomer")
    @ResponseBody
    public Long findCustomer(Long id) {
        return id;
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String id, @RequestParam String password, HttpServletRequest request) {
        Customer customer = customerRepository.getCustomerById(Long.parseLong(id));
        customer.setPassword(new BCryptPasswordEncoder().encode(password));
        customerRepository.save(customer);
        return "redirect:/logout";
    }

    @GetMapping(value = "/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping(value = "/403")
    public String accessDenied(Model model, Principal principal) {
        if (principal != null) {
            String message = "Hi " + principal.getName() + "<br> You do not have permission to access this page!";
            model.addAttribute("message", message);
        }
        return "403Page";
    }

    @PostMapping("/searchProductByName")
    public String searchProductByName(String name, Model model) {
        List<Product> productList = productRepository.findAllByNameContains(name);
        model.addAttribute("searchResult", name);
        model.addAttribute("productsList", productList);
        return "index";
    }
}
