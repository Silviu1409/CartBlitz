package com.savian.cartblitz.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import com.savian.cartblitz.model.Coupon;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "coupon")
public interface CouponServiceProxy {
    @GetMapping("/coupon")
    ResponseEntity<Coupon> getCoupon(@RequestHeader("coupon") String correlationId);
}
