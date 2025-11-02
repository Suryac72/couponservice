package com.monkcommerce.couponservice.strategy;

import com.monkcommerce.couponservice.dto.CartDTO;
import com.monkcommerce.couponservice.dto.UpdatedCartResponse;
import com.monkcommerce.couponservice.model.Coupon;
import com.monkcommerce.couponservice.model.CouponType;

public interface DiscountStrategy {
    
    /** Gets the coupon type this strategy handles. */
    CouponType getCouponType();
    
    /** Checks if the coupon is applicable to the cart. */
    boolean isApplicable(CartDTO cart, Coupon coupon);

    /** Calculates the discount amount for the applicable-coupons endpoint. */
    double calculateDiscount(CartDTO cart, Coupon coupon);
    
    /** Applies the discount and returns the fully updated cart. */
    UpdatedCartResponse applyDiscount(CartDTO cart, Coupon coupon);
}