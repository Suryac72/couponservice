package com.monkcommerce.couponservice.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "coupons")
public class Coupon {
    @Id
    private String id;
    private String code;
    private CouponType type;
    private LocalDate expiryDate;
    
    private Object details;
}