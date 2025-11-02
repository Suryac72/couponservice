package com.monkcommerce.couponservice.dto;

import java.time.LocalDate;

import com.monkcommerce.couponservice.model.CouponType;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO for POST /coupons [cite: 28, 79]
@Data
public class CreateCouponRequest {
    @NotBlank(message = "code must not be blank")
    private String code;

    @NotNull(message = "type is required")
    private CouponType type;

    @FutureOrPresent(message = "expiryDate cannot be in the past")
    private LocalDate expiryDate;

    @NotNull(message = "details is required")
    private Object details;
}