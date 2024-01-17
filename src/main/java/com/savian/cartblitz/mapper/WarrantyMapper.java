package com.savian.cartblitz.mapper;

import com.savian.cartblitz.dto.WarrantyDto;
import com.savian.cartblitz.model.Warranty;
import com.savian.cartblitz.repository.OrderRepository;
import com.savian.cartblitz.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class WarrantyMapper {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public WarrantyMapper(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }
    public WarrantyDto warrantyToWarrantyDto(Warranty warranty){
        WarrantyDto warrantyDto = new WarrantyDto();
        warrantyDto.setWarrantyId(warranty.getWarrantyId());
        warrantyDto.setOrderId(warranty.getOrder().getOrderId());
        warrantyDto.setProductId(warranty.getProduct().getProductId());
        warrantyDto.setDurationMonths(warranty.getDurationMonths());
        return warrantyDto;
    }

    public Warranty warrantyDtoToWarranty(WarrantyDto warrantyDto){
        Warranty warranty = new Warranty();
        warranty.setWarrantyId(warrantyDto.getWarrantyId());
        warranty.setOrder(orderRepository.getReferenceById(warrantyDto.getOrderId()));
        warranty.setProduct(productRepository.getReferenceById(warrantyDto.getProductId()));
        warranty.setDurationMonths(warrantyDto.getDurationMonths());
        return warranty;
    }
}
