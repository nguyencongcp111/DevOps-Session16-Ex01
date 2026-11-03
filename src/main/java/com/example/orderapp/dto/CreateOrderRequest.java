package com.example.orderapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank String customerName,
        @NotBlank String customerPhone,
        @NotEmpty @Valid List<Item> items
) {
    public record Item(
            @NotNull Long productId,
            @Min(1) int quantity
    ) {
    }
}
