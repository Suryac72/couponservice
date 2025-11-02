package com.monkcommerce.couponservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.monkcommerce.couponservice.dto.CreateCouponRequest;
import com.monkcommerce.couponservice.model.Coupon;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    /**
     * Maps the CreateCouponRequest DTO to the Coupon entity.
     * The 'code', 'type', 'expiryDate', and 'details' fields
     * have the same name, so MapStruct maps them automatically.
     */
    @Mapping(target = "id", ignore = true)
    Coupon toEntity(CreateCouponRequest request);
    
}