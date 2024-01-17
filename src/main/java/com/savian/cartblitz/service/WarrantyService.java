package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.WarrantyDto;

import java.util.List;
import java.util.Optional;

public interface WarrantyService {
    List<WarrantyDto> getAllWarranties();
    Optional<WarrantyDto> getWarrantyById(Long warrantyId);

    List<WarrantyDto> getWarrantiesByOrderId(Long orderId);
    List<WarrantyDto> getWarrantiesByProductId(Long productId);

    WarrantyDto saveWarranty(WarrantyDto warrantyDto);
    WarrantyDto updateWarranty(Long warrantyId, WarrantyDto warrantyDto);
    void removeWarrantyById(Long warrantyId);
}
