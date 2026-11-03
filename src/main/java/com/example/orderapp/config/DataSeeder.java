package com.example.orderapp.config;

import com.example.orderapp.entity.Product;
import com.example.orderapp.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedProducts(ProductRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                log.debug("Products already seeded, skipping");
                return;
            }
            repository.save(new Product("Bàn phím cơ", new BigDecimal("1200000"), 50));
            repository.save(new Product("Chuột không dây", new BigDecimal("350000"), 100));
            repository.save(new Product("Màn hình 24 inch", new BigDecimal("3200000"), 20));
            log.info("Seeded {} products", repository.count());
        };
    }
}
