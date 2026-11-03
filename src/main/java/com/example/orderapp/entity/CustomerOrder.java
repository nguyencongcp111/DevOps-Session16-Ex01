package com.example.orderapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerPhone;

    @Column(nullable = false)
    private String status = "CREATED";

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected CustomerOrder() {
    }

    public CustomerOrder(String customerName, String customerPhone) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
    }

    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }
}
