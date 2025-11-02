package com.monkcommerce.couponservice.strategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.monkcommerce.couponservice.model.CouponType;

@Component
public class DiscountStrategyFactory {

    private final Map<CouponType, DiscountStrategy> strategyMap;

    public DiscountStrategyFactory(List<DiscountStrategy> strategies) {
        strategyMap = new EnumMap<>(CouponType.class);
        strategies.forEach(strategy -> strategyMap.put(strategy.getCouponType(), strategy));
    }

    public DiscountStrategy getStrategy(CouponType couponType) {
        DiscountStrategy strategy = strategyMap.get(couponType);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for coupon type: " + couponType);
        }
        return strategy;
    }
}