package com.example.sport_store.controller.cashier;

import com.example.sport_store.entity.*;
import com.example.sport_store.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;

@Controller
@RequestMapping("/cashier")
public class CashierController {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PaymentTypeRepository paymentTypeRepository;
    @Autowired
    OrderStatusRepository orderStatusRepository;
    @Autowired
    CustomerRepository customerRepository;

    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        model.addAttribute("paymentTypes", paymentTypeRepository.findAll());
        Customer customer = customerRepository.findUserByLogin(principal.getName());
        model.addAttribute("user",
                customer.getSurname() + " " + customer.getName());
    }

    @GetMapping
    public String cashierPage(Model model, Principal principal) {
        User loginUser = (User) ((Authentication) principal).getPrincipal();
        model.addAttribute("title", principal.getName());
        return "cashier/cashier";
    }

    @PostMapping
    public String findOrder(@RequestParam(value = "order_id") String orderId, Model model, Principal principal) {
        model.addAttribute("title", principal.getName());
        Order order = orderRepository.findById(Long.parseLong(orderId)).orElse(null);
        if (order == null) {
            model.addAttribute("orderNotFound", "orderNotFound");
        } else {
            model.addAttribute("order", order);
        }
        return "cashier/cashier";
    }

    @GetMapping("/payment")
    public String paymentCash(Long orderId, Long paymentTypeId) {
        Order order = orderRepository.getOrderById(orderId);
        for (OrderPosition orderPosition : order.getOrderPositions()) {
            orderPosition
                    .getProductAttribute()
                    .getProduct()
                    .setRating(orderPosition.getProductAttribute().getProduct().getRating() + 1);
        }
        order.setOrderStatus(orderStatusRepository.getOrderStatusById(2L));
        order.setPaymentType(paymentTypeRepository.getPaymentTypeById(paymentTypeId));
        order.setOrderCloseDate(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);
        return "cashier/cashier";
    }

    @GetMapping("/cancelOrder")
    public String cancelOrder(Long orderId) {
        Order order = orderRepository.getOrderById(orderId);
        order.setOrderStatus(orderStatusRepository.getOrderStatusById(3L));
        for (OrderPosition orderPosition : order.getOrderPositions()) {
            int quantity = orderPosition.getProductAttribute().getQuantity();
            orderPosition.getProductAttribute().setQuantity(quantity + orderPosition.getQuantity());
        }
        order.setOrderCloseDate(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);
        return "cashier/cashier";
    }

    @GetMapping("/openOrders")
    public String openOrdersView(Model model) {
        model.addAttribute("openOrders", orderRepository.findAllByOrderCloseDateIsNull());
        return "cashier/cashier";
    }

    @GetMapping("/closedOrders")
    public String closedOrdersView(Model model) {
        model.addAttribute("closedOrders",
                orderRepository.findAllByOrderCloseDateIsNotNullAndPaymentTypeIsNotNull());
        return "cashier/cashier";
    }

    @GetMapping("/canceledOrders")
    public String canceledOrdersView(Model model) {
        model.addAttribute("canceledOrders",
                orderRepository.findAllByOrderCloseDateIsNotNullAndPaymentTypeIsNull());
        return "cashier/cashier";
    }
}
