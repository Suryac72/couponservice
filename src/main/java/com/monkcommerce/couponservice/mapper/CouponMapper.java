package com.monkcommerce.couponservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.monkcommerce.couponservice.dto.CreateCouponRequest;
import com.monkcommerce.couponservice.model.Coupon;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    @Mapping(target = "id", ignore = true)
    Coupon toEntity(CreateCouponRequest request);
    
}