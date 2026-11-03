package com.example.orderapp.repository;

import com.example.orderapp.entity.CustomerOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    @EntityGraph(attributePaths = "items")
    Optional<CustomerOrder> findWithItemsById(Long id);

    @EntityGraph(attributePaths = "items")
    List<CustomerOrder> findAllByOrderByIdDesc();
}
