package com.monkcommerce.couponservice.strategy;

import com.monkcommerce.couponservice.dto.CartDTO;
import com.monkcommerce.couponservice.dto.UpdatedCartResponse;
import com.monkcommerce.couponservice.model.Coupon;
import com.monkcommerce.couponservice.model.CouponType;

public interface DiscountStrategy {
    CouponType getCouponType();
    boolean isApplicable(CartDTO cart, Coupon coupon);
    double calculateDiscount(CartDTO cart, Coupon coupon);
    UpdatedCartResponse applyDiscount(CartDTO cart, Coupon coupon);
}