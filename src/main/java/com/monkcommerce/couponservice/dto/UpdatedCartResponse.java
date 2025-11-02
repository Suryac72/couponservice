package com.monkcommerce.couponservice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

// This DTO maps to the "updated_cart" object in the PDF response
@Data
public class UpdatedCartResponse {

    private List<UpdatedCartItem> items;
    
    // Use @JsonProperty to match the PDF's snake_case names
    // where our Java names are different
    @JsonProperty("total_price")
    private double originalTotalPrice;

    @JsonProperty("total_discount")
    private double totalDiscount;

    @JsonProperty("final_price")
    private double finalPrice;

    @Data
    public static class UpdatedCartItem {
        // This will map to "product_id"
        private String productId;
        private int quantity;
        private double price;
        // This will map to "total_discount"
        private double totalDiscount; 
    }
}
