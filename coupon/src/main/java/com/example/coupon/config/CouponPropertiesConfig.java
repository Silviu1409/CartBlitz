package com.example.coupon.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("coupon")
@Getter
@Setter
public class CouponPropertiesConfig {
    private String productCategory;
    private int discount;
    private String versionId;
}