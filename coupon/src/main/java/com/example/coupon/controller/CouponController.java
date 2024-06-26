package com.example.coupon.controller;

import com.example.coupon.config.CouponPropertiesConfig;
import com.example.coupon.model.Coupon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CouponController {
    @Autowired
    private CouponPropertiesConfig configuration;

    @GetMapping("/coupon")
    public ResponseEntity<Coupon> getDiscount(@RequestHeader("cartblitz") String correlationId){

        Coupon coupon =  new Coupon(configuration.getProductCategory(), configuration.getDiscount(), configuration.getVersionId());

        log.info("correlation-id discount: {}", correlationId);
        return ResponseEntity.status(HttpStatus.OK).body(coupon);
    }
}
