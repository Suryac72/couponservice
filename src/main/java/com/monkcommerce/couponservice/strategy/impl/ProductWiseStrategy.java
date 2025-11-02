package com.monkcommerce.couponservice.strategy.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monkcommerce.couponservice.dto.CartDTO;
import com.monkcommerce.couponservice.dto.CartItemDTO;
import com.monkcommerce.couponservice.dto.CouponDetailsDTO.ProductWiseDetails;
import com.monkcommerce.couponservice.dto.UpdatedCartResponse;
import com.monkcommerce.couponservice.model.Coupon;
import com.monkcommerce.couponservice.model.CouponType;
import com.monkcommerce.couponservice.strategy.DiscountStrategy;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductWiseStrategy implements DiscountStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public CouponType getCouponType() {
        return CouponType.PRODUCT_WISE;
    }
    
    private ProductWiseDetails getDetails(Coupon coupon) {
        return objectMapper.convertValue(coupon.getDetails(), ProductWiseDetails.class);
    }

    @Override
    public boolean isApplicable(CartDTO cart, Coupon coupon) {
        ProductWiseDetails details = getDetails(coupon);
        return cart.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(details.getProductId()));
    }

    @Override
    public double calculateDiscount(CartDTO cart, Coupon coupon) {
        if (!isApplicable(cart, coupon)) return 0;
        
        ProductWiseDetails details = getDetails(coupon);
        return cart.getItems().stream()
                .filter(item -> item.getProductId().equals(details.getProductId()))
                .mapToDouble(item -> (item.getPrice() * item.getQuantity()) * (details.getDiscount() / 100.0))
                .sum();
    }

    @Override
    public UpdatedCartResponse applyDiscount(CartDTO cart, Coupon coupon) {
        ProductWiseDetails details = getDetails(coupon);
        UpdatedCartResponse response = new UpdatedCartResponse();
        double originalTotal = cart.getTotalValue();
        double totalDiscount = 0;

        List<UpdatedCartResponse.UpdatedCartItem> updatedItems = new ArrayList<>();
        for (CartItemDTO item : cart.getItems()) {
            UpdatedCartResponse.UpdatedCartItem updatedItem = new UpdatedCartResponse.UpdatedCartItem();
            updatedItem.setProductId(item.getProductId());
            updatedItem.setQuantity(item.getQuantity());
            updatedItem.setPrice(item.getPrice());
            
            if (item.getProductId().equals(details.getProductId())) {
                double itemDiscount = (item.getPrice() * item.getQuantity()) * (details.getDiscount() / 100.0);
                updatedItem.setTotalDiscount(itemDiscount);
                totalDiscount += itemDiscount;
            } else {
                updatedItem.setTotalDiscount(0);
            }
            updatedItems.add(updatedItem);
        }

        response.setItems(updatedItems);
        response.setOriginalTotalPrice(originalTotal);
        response.setTotalDiscount(totalDiscount);
        response.setFinalPrice(originalTotal - totalDiscount);
        
        return response;
    }
}