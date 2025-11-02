package com.monkcommerce.couponservice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdatedCartResponse {

    private List<UpdatedCartItem> items;

    @JsonProperty("total_price")
    private double originalTotalPrice;

    @JsonProperty("total_discount")
    private double totalDiscount;

    @JsonProperty("final_price")
    private double finalPrice;

    @Data
    public static class UpdatedCartItem {
        private String productId;
        private int quantity;
        private double price;
        private double totalDiscount; 
    }
}
