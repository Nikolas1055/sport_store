package com.example.sport_store.controller.admin;

import com.example.sport_store.entity.*;
import com.example.sport_store.repository.*;
import com.example.sport_store.service.CustomValidationService;
import com.example.sport_store.service.SaveUpdateCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Integer MAX_ROWS_PER_PAGE = 5;
    final RoleRepository roleRepository;
    final CustomerRepository customerRepository;
    final AssignedRoleRepository assignedRoleRepository;
    final OrderStatusRepository orderStatusRepository;
    final PaymentTypeRepository paymentTypeRepository;
    final OrderRepository orderRepository;
    final CustomValidationService customValidationService;
    final SaveUpdateCustomerService saveUpdateCustomerService;

    @Autowired
    public AdminController(RoleRepository roleRepository,
                           CustomerRepository customerRepository,
                           AssignedRoleRepository assignedRoleRepository,
                           OrderStatusRepository orderStatusRepository,
                           PaymentTypeRepository paymentTypeRepository,
                           OrderRepository orderRepository,
                           CustomValidationService customValidationService,
                           SaveUpdateCustomerService saveUpdateCustomerService) {
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.assignedRoleRepository = assignedRoleRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.paymentTypeRepository = paymentTypeRepository;
        this.orderRepository = orderRepository;
        this.customValidationService = customValidationService;
        this.saveUpdateCustomerService = saveUpdateCustomerService;
    }

    /**
     * Определение объектов, которые должны быть частью модели(Model).
     */
    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        model.addAttribute("paymentTypes", paymentTypeRepository.findAll());
        Customer customer = customerRepository.findUserByLogin(principal.getName());
        model.addAttribute("user", customer.getSurname() + " " + customer.getName());
    }

    /**
     * Обработка запроса - главная страница интерфейса администратора.
     */
    @GetMapping
    public String adminMainPageView() {
        return "admin/admin";
    }

    /**
     * Обработка запроса - вывод списка всех сотрудников.
     */
    @GetMapping("/employees")
    public String employeesPageView(Model model, @RequestParam(defaultValue = "0") int page, Principal principal) {
        model.addAttribute("employees",
                assignedRoleRepository
                        .findAssignedRolesWithoutRoleUserAndPrincipal("ROLE_USER",
                                principal.getName(), PageRequest.of(page, MAX_ROWS_PER_PAGE)));
        model.addAttribute("currentPage", page);
        return "admin/employees";
    }

    /**
     * Обработка запроса - вывод списка всех покупателей.
     */
    @GetMapping("/customers")
    public String customersView(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("customers",
                assignedRoleRepository.findAssignedRolesByRole(roleRepository.getRoleByName("ROLE_USER"),
                        PageRequest.of(page, MAX_ROWS_PER_PAGE)));
        model.addAttribute("currentPage", page);
        return "admin/customers";
    }

    /**
     * Обработка запроса - создать нового сотрудника.
     */
    @GetMapping("/createEmployee")
    public String createEmployee(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/employee";
    }

    /**
     * Обработка запроса - создать нового покупателя.
     */
    @GetMapping("/createCustomer")
    public String createCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "admin/customer";
    }

    /**
     * Обработка запроса - сохранить нового сотрудника.
     */
    @PostMapping("/saveEmployee")
    public String saveEmployee(@Valid Customer customer,
                               BindingResult result,
                               Model model,
                               MultipartFile image,
                               @RequestParam String roleId,
                               @RequestParam String password,
                               @RequestParam String email,
                               @RequestParam String phone,
                               @RequestParam String login) {
        if (customValidationService.checkCustomerUniqueFields(result, login, phone, email, null).hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/employee";
        }
        saveUpdateCustomerService.saveNewCustomer(image, customer, password, roleId);
        return "redirect:/admin/employees";
    }

    /**
     * Обработка запроса - сохранить нового покупателя.
     */
    @PostMapping("/saveCustomer")
    public String saveCustomer(@Valid Customer customer,
                               BindingResult result,
                               MultipartFile image,
                               @RequestParam String password,
                               @RequestParam String email,
                               @RequestParam String phone,
                               @RequestParam String login) {
        if (customValidationService.checkCustomerUniqueFields(result, login, phone, email, null).hasErrors()) {
            return "admin/customer";
        }
        saveUpdateCustomerService.saveNewCustomer(image, customer, password, null);
        return "redirect:/admin/customers";
    }

    /**
     * Обработка запроса - редактирование данных сотрудника.
     */
    @GetMapping("/editEmployee")
    public String editEmployee(Long id, Model model) {
        Customer customer = customerRepository.getCustomerById(id);
        model.addAttribute("customer", customer);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("employeeRole",
                assignedRoleRepository.findAssignedRolesByCustomer(customer).get(0).getRole());
        return "admin/edit_employee";
    }

    /**
     * Обработка запроса - редактирование данных покупателя.
     */
    @GetMapping("/editCustomer")
    public String editCustomer(Long id, Model model) {
        model.addAttribute("customer", customerRepository.getCustomerById(id));
        return "admin/edit_customer";
    }

    /**
     * Обработка запроса - обновить данные сотрудника.
     */
    @PostMapping("/updateEmployee")
    public String updateEmployee(@Valid Customer customer,
                                 MultipartFile image,
                                 BindingResult result,
                                 Model model,
                                 @RequestParam Long id,
                                 @RequestParam String name,
                                 @RequestParam String surname,
                                 @RequestParam String phone,
                                 @RequestParam String email,
                                 @RequestParam String country,
                                 @RequestParam String city,
                                 @RequestParam String login,
                                 @RequestParam String roleId) {
        if (customValidationService.checkCustomerUniqueFields(result, login, phone, email, id).hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/edit_employee";
        }
        saveUpdateCustomerService
                .updateExistCustomer(customer, image, name, surname, phone, email, country, city, login, roleId);
        return "redirect:/admin/employees";
    }

    /**
     * Обработка запроса - обновить данные покупателя.
     */
    @PostMapping("/updateCustomer")
    public String updateCustomer(@Valid Customer customer,
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
            return "admin/edit_customer";
        }
        saveUpdateCustomerService
                .updateExistCustomer(customer, image, name, surname, phone, email, country, city, login, null);
        return "redirect:/admin/customers";
    }

    /**
     * Обработка запроса - заблокировать покупателя или сотрудника.
     */
    @GetMapping("/block")
    public String blockEmployee(Long id, HttpServletRequest request) {
        Customer customer = customerRepository.getCustomerById(id);
        customer.setActive(!customer.isActive());
        customerRepository.save(customer);
        return "redirect:" + request.getHeader("Referer");
    }

    /**
     * Обработка запроса - найти покупателя/сотрудника по Id.
     * (метод требует доработки или изменения в связи с исправлением ошибок).
     * Заглушка, возвращает id принятый как аргумент.
     */
    @GetMapping("/find")
    @ResponseBody
    public Long findCustomer(Long id) {
        return id;
    }

    /**
     * Обработка запроса - изменить пароль покупателя/сотрудника.
     */
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String id, @RequestParam String password, HttpServletRequest request) {
        Customer customer = customerRepository.getCustomerById(Long.parseLong(id));
        customer.setPassword(new BCryptPasswordEncoder().encode(password));
        customerRepository.save(customer);
        return "redirect:" + request.getHeader("Referer");
    }

    /**
     * Обработка запроса - вывод списка статусов заказа.
     */
    @GetMapping(value = "/orderStatuses")
    public String orderStatusesPageView(Model model) {
        model.addAttribute("statuses", orderStatusRepository.findAll());
        return "/admin/order_statuses";
    }

    /**
     * Обработка запроса - сохранить статус заказа.
     */
    @PostMapping("/saveOrderStatus")
    public String saveOrderStatus(@RequestParam String id, @RequestParam String name) {
        if (orderStatusRepository.findOrderStatusByName(name).orElse(null) == null) {
            OrderStatus orderStatus = id.isEmpty() ? new OrderStatus() :
                    orderStatusRepository.getOrderStatusById(Long.parseLong(id));
            orderStatus.setName(name);
            orderStatusRepository.save(orderStatus);
        }
        return "redirect:/admin/orderStatuses";
    }

    /**
     * Обработка запроса - удалить статус заказа.
     */
    @GetMapping("/deleteOrderStatus")
    public String deleteOrderStatus(Long id) {
        if (orderRepository
                .findOrderByOrderStatus(orderStatusRepository.findById(id).orElse(null)).isEmpty()) {
            orderStatusRepository.deleteById(id);
        }
        return "redirect:/admin/orderStatuses";
    }

    /**
     * Обработка запроса - найти статус заказа по Id.
     */
    @GetMapping("/findOrderStatus")
    @ResponseBody
    public OrderStatus findOrderStatus(Long id) {
        return orderStatusRepository.getOrderStatusById(id);
    }


    /**
     * Обработка запроса - вывод списка типов оплаты.
     */
    @GetMapping(value = "/paymentTypes")
    public String paymentTypesView(Model model) {
        model.addAttribute("types", paymentTypeRepository.findAll());
        return "/admin/payment_types";
    }

    /**
     * Обработка запроса - сохранить тип оплаты.
     */
    @PostMapping("/savePaymentType")
    public String savePaymentType(@RequestParam String id, @RequestParam String name) {
        if (paymentTypeRepository.findPaymentTypeByName(name).orElse(null) == null) {
            PaymentType paymentType = id.isEmpty() ? new PaymentType() :
                    paymentTypeRepository.getPaymentTypeById(Long.parseLong(id));
            paymentType.setName(name);
            paymentTypeRepository.save(paymentType);
        }
        return "redirect:/admin/paymentTypes";
    }

    /**
     * Обработка запроса - удалить тип оплаты.
     */
    @GetMapping("/deletePaymentType")
    public String deletePaymentType(Long id) {
        if (orderRepository
                .findOrderByPaymentType(paymentTypeRepository.findById(id).orElse(null)).isEmpty()) {
            paymentTypeRepository.deleteById(id);
        }
        return "redirect:/admin/paymentTypes";
    }

    /**
     * Обработка запроса - найти тип оплаты по Id.
     */
    @GetMapping("/findPaymentType")
    @ResponseBody
    public PaymentType findPaymentType(Long id) {
        return paymentTypeRepository.getPaymentTypeById(id);
    }
}