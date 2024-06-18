package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.WarrantyDto;
import com.savian.cartblitz.exception.*;
import com.savian.cartblitz.mapper.WarrantyMapper;
import com.savian.cartblitz.model.Warranty;
import com.savian.cartblitz.repository.WarrantyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WarrantyServiceImpl implements WarrantyService{
    private final WarrantyRepository warrantyRepository;
    private final WarrantyMapper warrantyMapper;

    public WarrantyServiceImpl(WarrantyRepository warrantyRepository, WarrantyMapper warrantyMapper) {
        this.warrantyRepository = warrantyRepository;
        this.warrantyMapper = warrantyMapper;
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
    public WarrantyDto saveWarranty(WarrantyDto warrantyDto) {
        Warranty savedWarranty = warrantyRepository.save(warrantyMapper.warrantyDtoToWarranty(warrantyDto));

        return warrantyMapper.warrantyToWarrantyDto(savedWarranty);
    }

    @Override
    public WarrantyDto updateWarranty(Long warrantyId, WarrantyDto warrantyDto) {
        Optional<Warranty> optWarranty = warrantyRepository.findById(warrantyId);

        if (optWarranty.isPresent()){
            Warranty prevWarranty = optWarranty.get();

            prevWarranty.setDurationMonths(warrantyDto.getDurationMonths());
            prevWarranty.setType(warrantyDto.getType());
            prevWarranty.setTerms(warrantyDto.getTerms());
            prevWarranty.setDetails(warrantyDto.getDetails());

            return warrantyMapper.warrantyToWarrantyDto(warrantyRepository.save(prevWarranty));
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
