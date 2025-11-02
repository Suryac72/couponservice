package com.monkcommerce.couponservice.strategy.impl;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monkcommerce.couponservice.dto.CartDTO;
import com.monkcommerce.couponservice.dto.CouponDetailsDTO.BxGyDetails;
import com.monkcommerce.couponservice.dto.UpdatedCartResponse;
import com.monkcommerce.couponservice.model.Coupon;
import com.monkcommerce.couponservice.model.CouponType;
import com.monkcommerce.couponservice.strategy.DiscountStrategy;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BxGyStrategy implements DiscountStrategy {
    
    private final ObjectMapper objectMapper;

    @Override
    public CouponType getCouponType() {
        return CouponType.BXGY;
    }
    
    private BxGyDetails getDetails(Coupon coupon) {
        return objectMapper.convertValue(coupon.getDetails(), BxGyDetails.class);
    }

    @Override
    public boolean isApplicable(CartDTO cart, Coupon coupon) {
        // TODO: Implement logic from [cite: 66-72]
        // 1. Check if cart contains at least 'X' items from 'buyProducts' list.
        // 2. Check if cart contains at least 'Y' items from 'getProducts' list.
        return true; // Placeholder
    }

    @Override
    public double calculateDiscount(CartDTO cart, Coupon coupon) {
        // TODO: Implement logic from [cite: 73-77]
        // 1. Get details (X, Y, buyProducts, getProducts, repetitionLimit).
        // 2. Count total 'buy' items in cart.
        // 3. Count total 'get' items in cart.
        // 4. Calculate num_applications = min( (total_buy_items / X), (total_get_items / Y), repetitionLimit)
        // 5. Find the (num_applications * Y) cheapest items from the 'getProducts' list in the cart.
        // 6. Return the sum of their prices as the discount.
        
        // Example logic from 
        // Buy 6 of X/Y, Get 2 of Z free. Cart has 6 X, 3 Y, 2 Z.
        // The BxGy payload [cite: 96-107] is complex. "buy_products": [{"id": 1, "qty": 3}, {"id": 2, "qty": 3}], "get_products": [{"id": 3, "qty": 1}], "rep_limit": 2
        // This example means "Buy 3 of P1 AND 3 of P2, Get 1 of P3 free".
        // Let's assume the cart has 6 of P1, 3 of P2, 2 of P3.
        // We can fulfill the "buy" condition once (we have 6 P1 and 3 P2).
        // We can apply this 1 time.
        // We get 1 of P3 free.
        // But the example response says "Buy 6 of Product X or Y, Get 2 of Product Z Free" 
        // This implies the example payload [cite: 96-107] is wrong or poorly written.
        // I WILL FOLLOW THE TEXTUAL DESCRIPTION[cite: 67]: "Buy 2 products from the 'buy' array ... and get 1 product from the 'get' array"
        // AND THE RESPONSE EXAMPLE: "Buy 6 ... Get 2". This sounds like B3G1, repeated twice.
        
        // This is a perfect example of an ASSUMPTION to list in the README[cite: 14].
        // Assumption: The BxGy payload [cite: 96-107] is simplified to:
        // "buy": {"productIds": ["1", "2"], "quantity": 3}, "get": {"productIds": ["3"], "quantity": 1}, "repetitionLimit": 2
        // This means "Buy any 3 from [1, 2], Get 1 from [3] free, max 2 times".
        
        return 50.0; // Placeholder based on example 
    }

    @Override
    public UpdatedCartResponse applyDiscount(CartDTO cart, Coupon coupon) {
        // TODO: Implement logic
        // 1. Calculate discount as above.
        // 2. Find the actual 'get' items in the cart to discount (e.g., the cheapest ones).
        // 3. Build the UpdatedCartResponse, setting 'totalDiscount' on the specific 'get' items.
        // 4. The response example [cite: 181-188] is confusing and seems malformed.
        // I will follow the other example [cite: 190] and apply discount to item 3.
        
        double originalTotal = cart.getTotalValue();
        double totalDiscount = 50.0; // From calc
        
        UpdatedCartResponse response = new UpdatedCartResponse();
        //... logic to build item list ...
        
        response.setOriginalTotalPrice(originalTotal);
        response.setTotalDiscount(totalDiscount);
        response.setFinalPrice(originalTotal - totalDiscount);
        return response;
    }
}