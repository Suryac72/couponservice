package com.monkcommerce.couponservice.dto;

import com.monkcommerce.couponservice.model.CouponType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicableCouponResponse {
    private String couponId;
    private CouponType type;
    private double discount;
}
