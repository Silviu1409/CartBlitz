package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.WarrantyDto;
import com.savian.cartblitz.exception.*;
import com.savian.cartblitz.mapper.WarrantyMapper;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.model.Warranty;
import com.savian.cartblitz.repository.OrderRepository;
import com.savian.cartblitz.repository.ProductRepository;
import com.savian.cartblitz.repository.WarrantyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WarrantyServiceImpl implements WarrantyService{
    private final WarrantyRepository warrantyRepository;
    private final WarrantyMapper warrantyMapper;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProductService orderProductService;

    public WarrantyServiceImpl(WarrantyRepository warrantyRepository, WarrantyMapper warrantyMapper, OrderRepository orderRepository, ProductRepository productRepository, OrderProductService orderProductService) {
        this.warrantyRepository = warrantyRepository;
        this.warrantyMapper = warrantyMapper;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderProductService = orderProductService;
    }

    @Override
    public List<WarrantyDto> getAllWarranties() {
        return warrantyRepository.findAll().stream().map(warrantyMapper::warrantyToWarrantyDto).toList();
    }

    @Override
    public Optional<WarrantyDto> getWarrantyById(Long warrantyId) {
        Optional<WarrantyDto> warrantyDto = warrantyRepository.findById(warrantyId).map(warrantyMapper::warrantyToWarrantyDto);

        if (warrantyDto.isPresent()) {
            return warrantyDto;
        }
        else {
            throw new WarrantyNotFoundException(warrantyId);
        }
    }

    @Override
    public List<WarrantyDto> getWarrantiesByOrderId(Long orderId) {
        orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        return warrantyRepository.findByOrderOrderId(orderId).stream().map(warrantyMapper::warrantyToWarrantyDto).toList();
    }

    @Override
    public List<WarrantyDto> getWarrantiesByProductId(Long productId) {
        productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

        return warrantyRepository.findByProductProductId(productId).stream().map(warrantyMapper::warrantyToWarrantyDto).toList();
    }

    @Override
    public WarrantyDto saveWarranty(WarrantyDto warrantyDto) {
        Order order = orderRepository.findById(warrantyDto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(warrantyDto.getOrderId()));

        Product product = productRepository.findById(warrantyDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(warrantyDto.getProductId()));

        Optional<OrderProduct> optOrderProduct = orderProductService.getOrderProductByOrderIdAndProductId(order.getOrderId(), product.getProductId());

        if (optOrderProduct.isPresent()){
            Warranty warranty = new Warranty();

            warranty.setOrder(order);
            warranty.setProduct(product);
            warranty.setDurationMonths(warrantyDto.getDurationMonths());

            return warrantyMapper.warrantyToWarrantyDto(warrantyRepository.save(warranty));
        }
        else{
            throw new OrderProductNotFoundException(order.getOrderId(), product.getProductId());
        }
    }

    @Override
    public WarrantyDto updateWarranty(Long warrantyId, WarrantyDto warrantyDto) {
        Optional<Warranty> optWarranty = warrantyRepository.findById(warrantyId);
        if (optWarranty.isPresent()){
            Order order = orderRepository.findById(warrantyDto.getOrderId())
                    .orElseThrow(() -> new OrderNotFoundException(warrantyDto.getOrderId()));

            Product product = productRepository.findById(warrantyDto.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(warrantyDto.getProductId()));

            Optional<OrderProduct> optOrderProduct = orderProductService.getOrderProductByOrderIdAndProductId(order.getOrderId(), product.getProductId());

            if (optOrderProduct.isPresent()) {
                Warranty prevWarranty = optWarranty.get();

                prevWarranty.setOrder(order);
                prevWarranty.setProduct(product);
                prevWarranty.setDurationMonths(warrantyDto.getDurationMonths());

                return warrantyMapper.warrantyToWarrantyDto(warrantyRepository.save(prevWarranty));
            }
            else {
                throw new OrderProductNotFoundException(order.getOrderId(), product.getProductId());
            }
        }
        else{
            throw new WarrantyNotFoundException(warrantyId);
        }
    }

    @Override
    public void removeWarrantyById(Long warrantyId) {
        Optional<Warranty> warranty = warrantyRepository.findById(warrantyId);

        if(warranty.isPresent()){
            warrantyRepository.deleteById(warrantyId);
        }
        else{
            throw new WarrantyNotFoundException(warrantyId);
        }
    }
}
