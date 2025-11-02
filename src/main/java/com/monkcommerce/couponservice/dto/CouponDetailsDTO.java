package com.monkcommerce.couponservice.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

public class CouponDetailsDTO {

    @Data
    public static class CartWiseDetails {
        @PositiveOrZero(message = "threshold must be zero or positive")
        private double threshold;
        @PositiveOrZero(message = "discount must be zero or positive")
        private double discount; 
    }

    @Data
    public static class ProductWiseDetails {
        @NotBlank(message = "productId must not be blank")
        private String productId; 
        @PositiveOrZero(message = "discount must be zero or positive")
        private double discount; 
    }

    @Data
    public static class BxGyDetails {
        @NotEmpty(message = "buyProducts must not be empty")
        @Valid
        private List<ProductRequirement> buyProducts;

        @NotEmpty(message = "getProducts must not be empty")
        @Valid
        private List<ProductRequirement> getProducts;

        @Min(value = 0, message = "repetitionLimit must be zero or positive")
        private int repetitionLimit;
    }

    @Data
    public static class ProductRequirement {
        @NotBlank(message = "productId must not be blank")
        private String productId;

        @Min(value = 1, message = "quantity must be at least 1")
        private int quantity;
    }
}
