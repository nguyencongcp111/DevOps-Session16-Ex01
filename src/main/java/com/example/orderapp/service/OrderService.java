package com.example.orderapp.service;

import com.example.orderapp.dto.CreateOrderRequest;
import com.example.orderapp.entity.CustomerOrder;
import com.example.orderapp.entity.OrderItem;
import com.example.orderapp.entity.Product;
import com.example.orderapp.repository.OrderRepository;
import com.example.orderapp.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CustomerOrder createOrder(CreateOrderRequest request) {
        log.debug("Creating order for customer={} with {} item(s)", request.customerName(), request.items().size());
        log.trace("Full order request: {}", request);

        CustomerOrder order = new CustomerOrder(request.customerName(), request.customerPhone());
        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderRequest.Item item : request.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> {
                        log.warn("Product {} not found", item.productId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Product not found: " + item.productId());
                    });

            log.trace("Checking stock for product={} requested={} available={}",
                    product.getId(), item.quantity(), product.getStock());

            if (product.getStock() < item.quantity()) {
                log.warn("Insufficient stock for product={} requested={} available={}",
                        product.getId(), item.quantity(), product.getStock());
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Insufficient stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - item.quantity());
            order.getItems().add(new OrderItem(order, product.getId(), product.getName(),
                    item.quantity(), product.getPrice()));
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.quantity())));
        }

        order.setTotal(total);
        CustomerOrder saved = orderRepository.save(order);
        log.info("Order created id={} customer={} total={}", saved.getId(), saved.getCustomerName(), saved.getTotal());
        return saved;
    }

    @Transactional(readOnly = true)
    public CustomerOrder getOrder(Long id) {
        log.debug("Fetching order id={}", id);
        return orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<CustomerOrder> listOrders() {
        log.debug("Listing all orders");
        return orderRepository.findAllByOrderByIdDesc();
    }
}
