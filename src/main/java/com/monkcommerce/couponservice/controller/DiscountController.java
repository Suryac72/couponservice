package com.monkcommerce.couponservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monkcommerce.couponservice.dto.ApplicableCouponResponse;
import com.monkcommerce.couponservice.dto.CartDTO;
import com.monkcommerce.couponservice.dto.UpdatedCartResponse;
import com.monkcommerce.couponservice.service.DiscountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping("/applicable-coupons")
    public List<ApplicableCouponResponse> getApplicableCoupons(@Valid @RequestBody CartDTO cart) {
        return discountService.getApplicableCoupons(cart);
    }

    @PostMapping("/apply-coupon/{id}")
    public UpdatedCartResponse applyCoupon(@PathVariable String id, @Valid @RequestBody CartDTO cart) {
        return discountService.applyCoupon(id, cart);
    }
}