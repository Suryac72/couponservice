package com.monkcommerce.couponservice.dto;

import com.monkcommerce.couponservice.model.CouponType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicableCouponResponse {
    // Renamed from couponId to match PDF's "coupon_id"
    private String couponId;
    private CouponType type;
    // Renamed from discountAmount to match PDF's "discount"
    private double discount;
}
