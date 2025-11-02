package com.monkcommerce.couponservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CartItemDTO {
    @NotBlank(message = "productId must not be blank")
    private String productId;

    @Min(value = 1, message = "quantity must be at least 1")
    private int quantity;

    @PositiveOrZero(message = "price must be zero or positive")
    private double price;
}