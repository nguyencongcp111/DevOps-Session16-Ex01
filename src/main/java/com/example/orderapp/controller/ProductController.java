package com.example.orderapp.controller;

import com.example.orderapp.dto.CreateProductRequest;
import com.example.orderapp.entity.Product;
import com.example.orderapp.repository.ProductRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> list() {
        log.debug("GET /api/products");
        return productRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody CreateProductRequest request) {
        Product saved = productRepository.save(new Product(request.name(), request.price(), request.stock()));
        log.info("Product created id={} name={}", saved.getId(), saved.getName());
        return saved;
    }
}
