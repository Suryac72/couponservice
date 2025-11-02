package com.monkcommerce.couponservice.strategy.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monkcommerce.couponservice.dto.CartDTO;
import com.monkcommerce.couponservice.dto.CartItemDTO;
import com.monkcommerce.couponservice.dto.CouponDetailsDTO.BxGyDetails;
import com.monkcommerce.couponservice.dto.CouponDetailsDTO.ProductRequirement;
import com.monkcommerce.couponservice.dto.UpdatedCartResponse;
import com.monkcommerce.couponservice.model.Coupon;
import com.monkcommerce.couponservice.model.CouponType;
import com.monkcommerce.couponservice.strategy.DiscountStrategy;

import lombok.Data;
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
        // Use ObjectMapper to convert the generic 'details' object into our specific DTO
        return objectMapper.convertValue(coupon.getDetails(), BxGyDetails.class);
    }

    /**
     * Checks if the BxGy coupon is applicable by calculating the potential discount.
     * It's applicable if at least one set of (Buy X, Get Y) can be fulfilled.
     */
    @Override
    public boolean isApplicable(CartDTO cart, Coupon coupon) {
        BxGyDiscountCalculation calc = calculateDiscountInternal(cart, coupon);
        return calc.getApplications() > 0;
    }

    /**
     * Calculates the total discount amount for the BxGy coupon.
     */
    @Override
    public double calculateDiscount(CartDTO cart, Coupon coupon) {
        return calculateDiscountInternal(cart, coupon).getTotalDiscount();
    }

    /**
     * Applies the discount to the cart and returns the updated cart state.
     * This implementation finds the cheapest "get" items in the cart to make free.
     */
    @Override
    public UpdatedCartResponse applyDiscount(CartDTO cart, Coupon coupon) {
        BxGyDetails details = getDetails(coupon);
        BxGyDiscountCalculation calc = calculateDiscountInternal(cart, coupon);
        double originalTotal = cart.getTotalValue();

        UpdatedCartResponse response = new UpdatedCartResponse();
        response.setOriginalTotalPrice(originalTotal);
        response.setTotalDiscount(calc.getTotalDiscount());
        response.setFinalPrice(originalTotal - calc.getTotalDiscount());

        // --- Logic to apply discount to specific items ---

        // 1. Find all "get" product IDs from the coupon
        List<String> getProductIds = details.getGetProducts().stream()
                .map(ProductRequirement::getProductId)
                .collect(Collectors.toList());

        // 2. Find all matching "get" items in the cart
        List<CartItemDTO> applicableGetItems = findApplicableCartItems(cart, getProductIds);

        // 3. Find the N cheapest items to discount
        int totalItemsToDiscount = calc.getTotalItemsToDiscount();
        List<DiscountableItem> itemsToDiscount = findCheapestItems(applicableGetItems, totalItemsToDiscount);
        Map<String, Long> discountedItemCounts = itemsToDiscount.stream()
                .collect(Collectors.groupingBy(DiscountableItem::getProductId, Collectors.counting()));

        // 4. Build the final updated item list
        List<UpdatedCartResponse.UpdatedCartItem> updatedItems = new ArrayList<>();
        for (CartItemDTO item : cart.getItems()) {
            UpdatedCartResponse.UpdatedCartItem updatedItem = new UpdatedCartResponse.UpdatedCartItem();
            updatedItem.setProductId(item.getProductId());
            updatedItem.setQuantity(item.getQuantity());
            updatedItem.setPrice(item.getPrice());

            // Check if this item is one of the ones we decided to discount
            if (discountedItemCounts.containsKey(item.getProductId())) {
                long count = discountedItemCounts.get(item.getProductId());
                updatedItem.setTotalDiscount(count * item.getPrice());
                // Remove the count so we don't apply it again if the same product ID appears twice
                discountedItemCounts.remove(item.getProductId());
            } else {
                updatedItem.setTotalDiscount(0);
            }
            updatedItems.add(updatedItem);
        }

        response.setItems(updatedItems);
        return response;
    }

    private BxGyDiscountCalculation calculateDiscountInternal(CartDTO cart, Coupon coupon) {
        BxGyDetails details = getDetails(coupon);

        // Create a map of productId -> quantity in cart for easy lookup
        Map<String, Integer> cartQuantities = cart.getItems().stream()
                .collect(Collectors.toMap(CartItemDTO::getProductId, CartItemDTO::getQuantity, Integer::sum));

        // 1. Calculate total available "BUY" batches
        int totalBuyBatches = 0;
        for (ProductRequirement buyReq : details.getBuyProducts()) {
            int quantityInCart = cartQuantities.getOrDefault(buyReq.getProductId(), 0);
            totalBuyBatches += Math.floorDiv(quantityInCart, buyReq.getQuantity());
        }

        // 2. Calculate total available "GET" batches and price per batch
        int totalGetBatches = 0;
        double pricePerGetBatch = 0;
        
        // To calculate price, we must find the cheapest "get" items
        List<String> getProductIds = details.getGetProducts().stream()
            .map(ProductRequirement::getProductId)
            .collect(Collectors.toList());

        List<CartItemDTO> applicableGetItems = findApplicableCartItems(cart, getProductIds);

        for (ProductRequirement getReq : details.getGetProducts()) {
            int quantityInCart = cartQuantities.getOrDefault(getReq.getProductId(), 0);
            totalGetBatches += Math.floorDiv(quantityInCart, getReq.getQuantity());
            
            // For price, find the matching item in the cart
            // This assumes get_products list has one item, like the example.
            // For complex multi-item "get" batches, this logic would need to be expanded.
            CartItemDTO itemInCart = applicableGetItems.stream()
                .filter(i -> i.getProductId().equals(getReq.getProductId()))
                .findFirst().orElse(null);
            
            if (itemInCart != null) {
                pricePerGetBatch += itemInCart.getPrice() * getReq.getQuantity();
            }
        }
        
        // 3. Determine number of applications
        int applications = Math.min(totalBuyBatches, totalGetBatches);
        applications = Math.min(applications, details.getRepetitionLimit());

        // 4. Calculate total discount
        double totalDiscount = applications * pricePerGetBatch;

        // 5. Calculate total items to be discounted (for applyDiscount method)
        int totalItemsToDiscount = 0;
        if (applications > 0) {
            for (ProductRequirement getReq : details.getGetProducts()) {
                totalItemsToDiscount += getReq.getQuantity() * applications;
            }
        }

        return new BxGyDiscountCalculation(applications, totalDiscount, totalItemsToDiscount);
    }

    /**
     * Helper to find all cart items that match a list of product IDs.
     */
    private List<CartItemDTO> findApplicableCartItems(CartDTO cart, List<String> productIds) {
        return cart.getItems().stream()
                .filter(item -> productIds.contains(item.getProductId()))
                .collect(Collectors.toList());
    }

    private List<DiscountableItem> findCheapestItems(List<CartItemDTO> applicableItems, int totalItemsToDiscount) {
        List<DiscountableItem> unrolledItems = new ArrayList<>();
        for (CartItemDTO item : applicableItems) {
            for (int i = 0; i < item.getQuantity(); i++) {
                unrolledItems.add(new DiscountableItem(item.getProductId(), item.getPrice()));
            }
        }

        // Sort by price (cheapest first) and take the number we need to discount
        return unrolledItems.stream()
                .sorted(Comparator.comparingDouble(DiscountableItem::getPrice))
                .limit(totalItemsToDiscount)
                .collect(Collectors.toList());
    }

    /**
     * Inner class to hold the result of the discount calculation.
     */
    @Data
    private static class BxGyDiscountCalculation {
        private final int applications;
        private final double totalDiscount;
        private final int totalItemsToDiscount;
    }

    /**
     * Inner class to represent a single, unrolled item for sorting by price.
     */
    @Data
    private static class DiscountableItem {
        private final String productId;
        private final double price;
    }
}

