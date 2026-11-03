package com.example.orderapp.controller;

import com.example.orderapp.dto.CreateOrderRequest;
import com.example.orderapp.entity.CustomerOrder;
import com.example.orderapp.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOrder create(@Valid @RequestBody CreateOrderRequest request) {
        log.debug("POST /api/orders");
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public CustomerOrder get(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @GetMapping
    public List<CustomerOrder> list() {
        return orderService.listOrders();
    }
}
