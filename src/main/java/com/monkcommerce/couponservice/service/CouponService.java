package com.monkcommerce.couponservice.service;

import java.util.List;
import java.util.Optional;

import com.monkcommerce.couponservice.dto.CreateCouponRequest;
import com.monkcommerce.couponservice.model.Coupon;

public interface CouponService {
    Coupon createCoupon(CreateCouponRequest request);
    List<Coupon> getAllCoupons();
    Optional<Coupon> getCouponById(String id);
    Coupon updateCoupon(String id, CreateCouponRequest request);
    void deleteCoupon(String id); 
}