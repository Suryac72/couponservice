package com.monkcommerce.couponservice.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.monkcommerce.couponservice.dto.CreateCouponRequest;
import com.monkcommerce.couponservice.model.Coupon;
import com.monkcommerce.couponservice.service.CouponService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Coupon createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        return couponService.createCoupon(request);
    }

    @GetMapping
    public List<Coupon> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @GetMapping("/{id}")
    public Optional<Coupon> getCouponById(@PathVariable String id) {
        return couponService.getCouponById(id);
    }

    @PutMapping("/{id}")
    public Coupon updateCoupon(@PathVariable String id, @Valid @RequestBody CreateCouponRequest request) {
        return couponService.updateCoupon(id, request);
    }

    @DeleteMapping("/{id}") 
    @ResponseStatus(HttpStatus.NO_CONTENT) 
    public void deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
    }

}