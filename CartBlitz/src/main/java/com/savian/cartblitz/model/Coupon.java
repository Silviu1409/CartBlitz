package com.savian.cartblitz.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Coupon {
    private String productCategory;
    private int discount;
    private String versionId;
}
