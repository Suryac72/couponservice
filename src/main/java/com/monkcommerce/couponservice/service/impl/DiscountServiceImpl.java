package com.monkcommerce.couponservice.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.monkcommerce.couponservice.dto.ApplicableCouponResponse;
import com.monkcommerce.couponservice.dto.CartDTO;
import com.monkcommerce.couponservice.dto.UpdatedCartResponse;
import com.monkcommerce.couponservice.exception.CouponNotApplicableException;
import com.monkcommerce.couponservice.exception.CouponNotFoundException;
import com.monkcommerce.couponservice.model.Coupon;
import com.monkcommerce.couponservice.repository.CouponRepository;
import com.monkcommerce.couponservice.service.DiscountService;
import com.monkcommerce.couponservice.strategy.DiscountStrategy;
import com.monkcommerce.couponservice.strategy.DiscountStrategyFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final CouponRepository couponRepository;
    private final DiscountStrategyFactory strategyFactory;

    @Override
    public List<ApplicableCouponResponse> getApplicableCoupons(CartDTO cart) {
        List<Coupon> allCoupons = couponRepository.findAll().stream()
                .filter(c -> c.getExpiryDate() == null || c.getExpiryDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());

        return allCoupons.stream()
                .map(coupon -> {
                    DiscountStrategy strategy = strategyFactory.getStrategy(coupon.getType());
                    if (strategy.isApplicable(cart, coupon)) {
                        double discount = strategy.calculateDiscount(cart, coupon);
                        return new ApplicableCouponResponse(coupon.getId(), coupon.getType(), discount);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public UpdatedCartResponse applyCoupon(String couponId, CartDTO cart) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id: " + couponId));
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CouponNotApplicableException("Coupon is expired.");
        }

        DiscountStrategy strategy = strategyFactory.getStrategy(coupon.getType());

        if (!strategy.isApplicable(cart, coupon)) {
            throw new CouponNotApplicableException("Coupon conditions not met.");
        }

        return strategy.applyDiscount(cart, coupon);
    }
}