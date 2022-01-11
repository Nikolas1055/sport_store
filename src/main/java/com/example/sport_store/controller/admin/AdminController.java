package com.example.sport_store.controller.admin;

import com.example.sport_store.entity.*;
import com.example.sport_store.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final String UPLOAD_FOLDER = "upload/customer_image/";
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    AssignedRoleRepository assignedRoleRepository;
    @Autowired
    OrderStatusRepository orderStatusRepository;
    @Autowired
    PaymentTypeRepository paymentTypeRepository;

    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        model.addAttribute("paymentTypes", paymentTypeRepository.findAll());
        Customer customer = customerRepository.findUserByLogin(principal.getName());
        model.addAttribute("user",
                customer.getSurname() + " " + customer.getName());
    }

    @GetMapping
    public String adminPage() {
        return "admin/admin";
    }

    @GetMapping("/employees")
    public String employeesView(Model model, @RequestParam(defaultValue = "0") int page) {
        List<AssignedRole> assignedRoleList = assignedRoleRepository
                .findAll()
                .stream()
                .filter(assignedRole -> assignedRole.getRole() != roleRepository.getRoleByName("ROLE_USER"))
                .collect(Collectors.toList());
        Page<AssignedRole> assignedRolePage =
                new PageImpl<>(assignedRoleList, PageRequest.of(page, 10), assignedRoleList.size());
        model.addAttribute("employees", assignedRolePage);
        model.addAttribute("currentPage", page);
        return "admin/employees";
    }

    @GetMapping("/customers")
    public String customersView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("customers",
                assignedRoleRepository.findAssignedRolesByRole(roleRepository.getRoleByName("ROLE_USER"),
                        PageRequest.of(page, 10)));
        model.addAttribute("currentPage", page);
        return "admin/customers";
    }

    @GetMapping("/createEmployee")
    public String createEmployee(Model model) {
        model.addAttribute("employee", new Customer());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/employee";
    }

    @GetMapping("/createCustomer")
    public String createCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "admin/customer";
    }

    @PostMapping("/saveEmployee")
    public String saveEmployee(Customer customer,
                               MultipartFile image,
                               @RequestParam String roleId,
                               @RequestParam String password) {
        if (!image.isEmpty()) {
            try {
                String newImageFileName = UUID.randomUUID().toString().replaceAll("-", "") +
                        image.getOriginalFilename();
                Files.copy(image.getInputStream(), Paths.get(UPLOAD_FOLDER, newImageFileName));
                customer.setPhoto("/" + UPLOAD_FOLDER + newImageFileName.replaceAll(" ", ""));
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
            }
        } else {
            customer.setPhoto("/" + UPLOAD_FOLDER + "no_avatar.png");
        }
        customer.setPassword(new BCryptPasswordEncoder().encode(password));
        customer.setRegDate(new Date());
        customerRepository.save(customer);
        assignedRoleRepository.save(new AssignedRole(customer, roleRepository.getById(Long.parseLong(roleId))));
        return "redirect:/admin/employees";
    }

    @PostMapping("/saveCustomer")
    public String saveCustomer(Customer customer,
                               MultipartFile image,
                               @RequestParam String password) {
        if (!image.isEmpty()) {
            try {
                String newImageFileName = UUID.randomUUID().toString().replaceAll("-", "") +
                        image.getOriginalFilename();
                Files.copy(image.getInputStream(), Paths.get(UPLOAD_FOLDER, newImageFileName));
                customer.setPhoto("/" + UPLOAD_FOLDER + newImageFileName.replaceAll(" ", ""));
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
            }
        } else {
            customer.setPhoto("/" + UPLOAD_FOLDER + "no_avatar.png");
        }
        customer.setPassword(new BCryptPasswordEncoder().encode(password));
        customer.setRegDate(new Date());
        customerRepository.save(customer);
        assignedRoleRepository.save(new AssignedRole(customer, roleRepository.getRoleByName("ROLE_USER")));
        return "redirect:/admin/customers";
    }

    @GetMapping("/editEmployee")
    public String editEmployee(Long id, Model model) {
        Customer customer = customerRepository.getCustomerById(id);
        model.addAttribute("employee", customer);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("employeeRole",
                assignedRoleRepository.findAssignedRolesByCustomer(customer).get(0).getRole());
        return "admin/edit_employee";
    }

    @GetMapping("/editCustomer")
    public String editCustomer(Long id, Model model) {
        model.addAttribute("customer", customerRepository.getCustomerById(id));
        return "admin/edit_customer";
    }

    @PostMapping("/updateEmployee")
    public String updateEmployee(MultipartFile image,
                                 @RequestParam Long id,
                                 @RequestParam String name,
                                 @RequestParam String surname,
                                 @RequestParam String phone,
                                 @RequestParam String email,
                                 @RequestParam String country,
                                 @RequestParam String city,
                                 @RequestParam String login,
                                 @RequestParam String roleId) {
        Customer customer = customerRepository.getCustomerById(id);
        if (!image.isEmpty()) {
            try {
                String newImageFileName = UUID.randomUUID().toString().replaceAll("-", "") +
                        image.getOriginalFilename();
                Files.copy(image.getInputStream(), Paths.get(UPLOAD_FOLDER, newImageFileName));
                customer.setPhoto("/" + UPLOAD_FOLDER + newImageFileName.replaceAll(" ", ""));
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
            }
        }
        customer.setName(name);
        customer.setSurname(surname);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setCountry(country);
        customer.setCity(city);
        customer.setLogin(login);
        customerRepository.save(customer);
        AssignedRole assignedRole = assignedRoleRepository.findAssignedRoleByCustomer(customer);
        assignedRole.setRole(roleRepository.getById(Long.parseLong(roleId)));
        assignedRoleRepository.save(assignedRole);
        return "redirect:/admin/employees";
    }

    @PostMapping("/updateCustomer")
    public String updateCustomer(MultipartFile image,
                                 @RequestParam Long id,
                                 @RequestParam String name,
                                 @RequestParam String surname,
                                 @RequestParam String phone,
                                 @RequestParam String email,
                                 @RequestParam String country,
                                 @RequestParam String city,
                                 @RequestParam String login) {
        Customer customer = customerRepository.getCustomerById(id);
        if (!image.isEmpty()) {
            try {
                String newImageFileName = UUID.randomUUID().toString().replaceAll("-", "") +
                        image.getOriginalFilename();
                Files.copy(image.getInputStream(), Paths.get(UPLOAD_FOLDER, newImageFileName));
                customer.setPhoto("/" + UPLOAD_FOLDER + newImageFileName.replaceAll(" ", ""));
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
            }
        }
        customer.setName(name);
        customer.setSurname(surname);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setCountry(country);
        customer.setCity(city);
        customer.setLogin(login);
        customerRepository.save(customer);
        return "redirect:/admin/customers";
    }

    @GetMapping("/block")
    public String blockEmployee(Long id, HttpServletRequest request) {
        Customer customer = customerRepository.getCustomerById(id);
        customer.setActive(!customer.isActive());
        customerRepository.save(customer);
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/find")
    @ResponseBody
    public Long findCustomer(Long id) {
        return id;
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String id, @RequestParam String password, HttpServletRequest request) {
        Customer customer = customerRepository.getCustomerById(Long.parseLong(id));
        customer.setPassword(new BCryptPasswordEncoder().encode(password));
        customerRepository.save(customer);
        return "redirect:" + request.getHeader("Referer");
    }


    @GetMapping(value = "/orderStatuses")
    public String orderStatusesView(Model model) {
        model.addAttribute("statuses", orderStatusRepository.findAll());
        return "/admin/order_statuses";
    }

    @PostMapping("/saveOrderStatus")
    public String saveOrderStatus(@RequestParam String id, @RequestParam String name) {
        OrderStatus orderStatus = id.isEmpty() ? new OrderStatus() :
                orderStatusRepository.getOrderStatusById(Long.parseLong(id));
        orderStatus.setName(name);
        orderStatusRepository.save(orderStatus);
        return "redirect:/admin/orderStatuses";
    }

    @GetMapping("/deleteOrderStatus")
    public String deleteOrderStatus(Long id) {
        orderStatusRepository.deleteById(id);
        return "redirect:/admin/orderStatuses";
    }

    @GetMapping("/findOrderStatus")
    @ResponseBody
    public OrderStatus findOrderStatus(Long id) {
        return orderStatusRepository.getOrderStatusById(id);
    }


    @GetMapping(value = "/paymentTypes")
    public String paymentTypesView(Model model) {
        model.addAttribute("types", paymentTypeRepository.findAll());
        return "/admin/payment_types";
    }

    @PostMapping("/savePaymentType")
    public String savePaymentType(@RequestParam String id, @RequestParam String name) {
        PaymentType paymentType = id.isEmpty() ? new PaymentType() :
                paymentTypeRepository.getPaymentTypeById(Long.parseLong(id));
        paymentType.setName(name);
        paymentTypeRepository.save(paymentType);
        return "redirect:/admin/paymentTypes";
    }

    @GetMapping("/deletePaymentType")
    public String deletePaymentType(Long id) {
        paymentTypeRepository.deleteById(id);
        return "redirect:/admin/paymentTypes";
    }

    @GetMapping("/findPaymentType")
    @ResponseBody
    public PaymentType findPaymentType(Long id) {
        return paymentTypeRepository.getPaymentTypeById(id);
    }
}
