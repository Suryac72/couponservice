package com.monkcommerce.couponservice.strategy.impl;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monkcommerce.couponservice.dto.CartDTO;
import com.monkcommerce.couponservice.dto.CouponDetailsDTO.CartWiseDetails;
import com.monkcommerce.couponservice.dto.UpdatedCartResponse;
import com.monkcommerce.couponservice.model.Coupon;
import com.monkcommerce.couponservice.model.CouponType;
import com.monkcommerce.couponservice.strategy.DiscountStrategy;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartWiseStrategy implements DiscountStrategy {

    private final ObjectMapper objectMapper; // For converting Object details

    @Override
    public CouponType getCouponType() {
        return CouponType.CART_WISE;
    }

    @Override
    public boolean isApplicable(CartDTO cart, Coupon coupon) {
        CartWiseDetails details = getDetails(coupon);
        return cart.getTotalValue() > details.getThreshold();
    }

    @Override
    public double calculateDiscount(CartDTO cart, Coupon coupon) {
        if (!isApplicable(cart, coupon)) return 0;
        
        CartWiseDetails details = getDetails(coupon);
        double totalValue = cart.getTotalValue();
        return totalValue * (details.getDiscount() / 100.0);
    }

    @Override
    public UpdatedCartResponse applyDiscount(CartDTO cart, Coupon coupon) {
        double discountAmount = calculateDiscount(cart, coupon);
        double originalTotal = cart.getTotalValue();

        UpdatedCartResponse response = new UpdatedCartResponse();
        // For cart-wise, the discount applies to the total, not specific items.
        // We can prorate it across items if needed, but the spec [cite: 189]
        // implies a single 'total_discount'.
        
        response.setItems(cart.getItems().stream().map(item -> {
            UpdatedCartResponse.UpdatedCartItem updatedItem = new UpdatedCartResponse.UpdatedCartItem();
            updatedItem.setProductId(item.getProductId());
            updatedItem.setQuantity(item.getQuantity());
            updatedItem.setPrice(item.getPrice());
            updatedItem.setTotalDiscount(0); // Item-level discount is 0
            return updatedItem;
        }).collect(Collectors.toList()));

        response.setOriginalTotalPrice(originalTotal);
        response.setTotalDiscount(discountAmount);
        response.setFinalPrice(originalTotal - discountAmount);
        
        return response;
    }
    
    // Helper to safely cast the details
    private CartWiseDetails getDetails(Coupon coupon) {
        return objectMapper.convertValue(coupon.getDetails(), CartWiseDetails.class);
    }
}