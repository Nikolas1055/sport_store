package com.example.sport_store.controller.cashier;

import com.example.sport_store.entity.*;
import com.example.sport_store.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;

@Controller
@RequestMapping("/cashier")
public class CashierController {
    final OrderRepository orderRepository;
    final PaymentTypeRepository paymentTypeRepository;
    final OrderStatusRepository orderStatusRepository;
    final CustomerRepository customerRepository;
    final ProductRepository productRepository;

    @Autowired
    public CashierController(OrderRepository orderRepository,
                             PaymentTypeRepository paymentTypeRepository,
                             OrderStatusRepository orderStatusRepository,
                             CustomerRepository customerRepository,
                             ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.paymentTypeRepository = paymentTypeRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    /**
     * Определение объектов, которые должны быть частью модели(Model).
     */
    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        model.addAttribute("paymentTypes", paymentTypeRepository.findAll());
        Customer customer = customerRepository.findUserByLogin(principal.getName());
        model.addAttribute("user",
                customer.getSurname() + " " + customer.getName());
    }

    /**
     * Обработка запроса - главная страница интерфейса кассира.
     */
    @GetMapping
    public String cashierPageView() {
        return "cashier/cashier";
    }

    /**
     * Обработка запроса - найти заказ по номеру (Id).
     */
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

    /**
     * Обработка запроса - оплата заказа выбранным типом оплаты.
     */
    @GetMapping("/payment")
    public String payment(Long orderId, Long paymentTypeId) {
        Order order = orderRepository.getOrderById(orderId);
        for (OrderPosition orderPosition : order.getOrderPositions()) {
            Product product = productRepository
                    .getProductById(orderPosition.getProductAttribute().getProduct().getId());
            product.setRating(orderPosition.getProductAttribute().getProduct().getRating() + 1);
            productRepository.save(product);
        }
        order.setOrderStatus(orderStatusRepository.getOrderStatusById(2L));
        order.setPaymentType(paymentTypeRepository.getPaymentTypeById(paymentTypeId));
        order.setOrderCloseDate(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);
        return "cashier/cashier";
    }

    /**
     * Обработка запроса - отмена заказа.
     */
    @GetMapping("/cancelOrder")
    public String cancelOrder(Long orderId) {
        Order order = orderRepository.getOrderById(orderId);
        for (OrderPosition orderPosition : order.getOrderPositions()) {
            Product product = productRepository
                    .getProductById(orderPosition.getProductAttribute().getProduct().getId());
            product.setRating(orderPosition.getProductAttribute().getProduct().getRating() - 1);
            productRepository.save(product);
        }
        order.setOrderStatus(orderStatusRepository.getOrderStatusById(3L));
        order.setOrderCloseDate(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);
        return "cashier/cashier";
    }

    /**
     * Обработка запроса - вывод списка открытых заказов.
     */
    @GetMapping("/openOrders")
    public String openOrdersView(Model model) {
        model.addAttribute("openOrders", orderRepository.findAllByOrderCloseDateIsNull());
        return "cashier/cashier";
    }

    /**
     * Обработка запроса - вывод списка оплаченных(закрытых) заказов.
     */
    @GetMapping("/closedOrders")
    public String closedOrdersView(Model model) {
        model.addAttribute("closedOrders",
                orderRepository.findAllByOrderCloseDateIsNotNullAndPaymentTypeIsNotNull());
        return "cashier/cashier";
    }

    /**
     * Обработка запроса - вывод списка отмененных заказов.
     */
    @GetMapping("/canceledOrders")
    public String canceledOrdersView(Model model) {
        model.addAttribute("canceledOrders",
                orderRepository.findAllByOrderCloseDateIsNotNullAndPaymentTypeIsNull());
        return "cashier/cashier";
    }
}
