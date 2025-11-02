package com.monkcommerce.couponservice.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull; 
import org.springframework.stereotype.Service;

import com.monkcommerce.couponservice.dto.CreateCouponRequest;
import com.monkcommerce.couponservice.exception.CouponNotFoundException;
import com.monkcommerce.couponservice.mapper.CouponMapper;
import com.monkcommerce.couponservice.model.Coupon;
import com.monkcommerce.couponservice.repository.CouponRepository;
import com.monkcommerce.couponservice.service.CouponService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    @SuppressWarnings("null")
    @Override
    public Coupon createCoupon(CreateCouponRequest request) {
        Coupon coupon = couponMapper.toEntity(request);
        return couponRepository.save(coupon);
    }

    @Override
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    public Optional<Coupon> getCouponById(String id) { 
        Optional<Coupon> coupon = couponRepository.findById(id);
        if(coupon == null || !coupon.isPresent()) {
            throw new CouponNotFoundException("Coupon not found with id: " + id);
        }
        return coupon;
    }

    @Override
    public Coupon updateCoupon(@SuppressWarnings("null") @NonNull String id, CreateCouponRequest request) {
        
        getCouponById(id); 

        Coupon couponToUpdate = couponMapper.toEntity(request);
        couponToUpdate.setId(id);
        return couponRepository.save(couponToUpdate);
    }

    @Override
    public void deleteCoupon(@SuppressWarnings("null") @NonNull String id) { 
        
        if (!couponRepository.existsById(id)) {
            throw new CouponNotFoundException("Coupon not found with id: " + id);
        }
        couponRepository.deleteById(id);
    }
    
}