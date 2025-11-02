package com.monkcommerce.couponservice.service;

import java.util.List;

import com.monkcommerce.couponservice.dto.ApplicableCouponResponse;
import com.monkcommerce.couponservice.dto.CartDTO;
import com.monkcommerce.couponservice.dto.UpdatedCartResponse;

public interface DiscountService {
    List<ApplicableCouponResponse> getApplicableCoupons(CartDTO cart);
    UpdatedCartResponse applyCoupon(String couponId, CartDTO cart);
}