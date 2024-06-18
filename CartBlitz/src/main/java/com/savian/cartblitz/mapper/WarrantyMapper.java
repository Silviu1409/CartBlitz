package com.savian.cartblitz.mapper;

import com.savian.cartblitz.dto.WarrantyDto;
import com.savian.cartblitz.model.Warranty;
import org.springframework.stereotype.Component;

@Component
public class WarrantyMapper {
    public WarrantyDto warrantyToWarrantyDto(Warranty warranty){
        WarrantyDto warrantyDto = new WarrantyDto();
        warrantyDto.setWarrantyId(warranty.getWarrantyId());
        warrantyDto.setDurationMonths(warranty.getDurationMonths());
        warrantyDto.setType(warranty.getType());
        warrantyDto.setTerms(warranty.getTerms());
        warrantyDto.setDetails(warrantyDto.getDetails());
        return warrantyDto;
    }

    public Warranty warrantyDtoToWarranty(WarrantyDto warrantyDto){
        Warranty warranty = new Warranty();
        warranty.setWarrantyId(warrantyDto.getWarrantyId());
        warranty.setDurationMonths(warrantyDto.getDurationMonths());
        warranty.setType(warrantyDto.getType());
        warranty.setTerms(warrantyDto.getTerms());
        warranty.setDetails(warrantyDto.getDetails());
        return warranty;
    }
}
