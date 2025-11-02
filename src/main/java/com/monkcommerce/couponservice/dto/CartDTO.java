package com.monkcommerce.couponservice.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CartDTO {
    @NotEmpty(message = "cart items must not be empty")
    @Valid
    private List<CartItemDTO> items;
    public double getTotalValue() {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}