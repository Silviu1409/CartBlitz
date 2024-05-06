package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.WarrantyDto;
import com.savian.cartblitz.exception.WarrantyNotFoundException;
import com.savian.cartblitz.mapper.WarrantyMapper;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.repository.WarrantyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("h2")
@Slf4j
public class WarrantyServiceUnitTest {
    @InjectMocks
    private WarrantyServiceImpl warrantyService;
    @Mock
    private WarrantyRepository warrantyRepository;
    @Mock
    private WarrantyMapper warrantyMapper;

    @Test
    public void testGetAllWarranties() {
        List<Warranty> warranties = new ArrayList<>();
        warranties.add(getDummyWarranty());

        log.info("Starting testGetAllWarranties");

        Mockito.when(warrantyRepository.findAll()).thenReturn(warranties);

        List<WarrantyDto> result = warrantyService.getAllWarranties();
        warranties.forEach(warranty -> log.info(String.valueOf(warranty.getWarrantyId())));

        Mockito.verify(warrantyRepository).findAll();
        Assertions.assertEquals(warranties.stream().map(warrantyMapper::warrantyToWarrantyDto).toList(), result);

        log.info("Finished testGetAllWarranties successfully");
    }

    @Test
    public void testGetWarrantyByIdFound() {
        Warranty warranty = getDummyWarranty();
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        log.info("Starting testGetWarrantyByIdFound");

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty));
        Mockito.when(warrantyMapper.warrantyToWarrantyDto(Mockito.any(Warranty.class))).thenReturn(warrantyDto);

        Optional<WarrantyDto> result = warrantyService.getWarrantyById(warranty.getWarrantyId());
        result.ifPresent(value -> log.info(String.valueOf(value.getWarrantyId())));

        Mockito.verify(warrantyRepository).findById(warranty.getWarrantyId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(warrantyDto, result.get());

        log.info("Finished testGetWarrantyByIdFound successfully");
    }

    @Test
    public void testGetWarrantyByIdNotFound() {
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        log.info("Starting testGetWarrantyByIdNotFound");

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(WarrantyNotFoundException.class, () -> warrantyService.getWarrantyById(warrantyDto.getWarrantyId()));
        log.error("Warranty with given ID was not found");

        Mockito.verify(warrantyRepository).findById(warrantyDto.getWarrantyId());

        log.info("Finished testGetWarrantyByIdNotFound successfully");
    }

    @Test
    void testSaveWarrantySuccess() {
        Warranty warranty = getDummyWarranty();
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        log.info("Starting testSaveWarrantySuccess");

        Mockito.when(warrantyRepository.save(Mockito.any())).thenReturn(warranty);
        Mockito.when(warrantyMapper.warrantyToWarrantyDto(Mockito.any())).thenReturn(warrantyDto);

        WarrantyDto result = warrantyService.saveWarranty(warrantyDto);
        log.info(String.valueOf(result.getWarrantyId()));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(warrantyDto.getWarrantyId(), result.getWarrantyId());

        log.info("Finished testSaveWarrantySuccess successfully");
    }

    @Test
    void testUpdateWarrantySuccess() {
        Warranty existingWarranty = getDummyWarranty();
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        log.info("Starting testUpdateWarrantySuccess");

        Mockito.when(warrantyRepository.findById(Mockito.any())).thenReturn(Optional.of(existingWarranty));
        Mockito.when(warrantyRepository.save(Mockito.any())).thenReturn(existingWarranty);
        Mockito.when(warrantyMapper.warrantyToWarrantyDto(Mockito.any())).thenReturn(warrantyDto);

        WarrantyDto result = warrantyService.updateWarranty(existingWarranty.getWarrantyId(), warrantyDto);
        log.info(String.valueOf(result.getWarrantyId()));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(warrantyDto.getWarrantyId(), result.getWarrantyId());

        log.info("Finished testUpdateWarrantySuccess successfully");
    }

    @Test
    public void testUpdateWarrantyNotFound() {
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        log.info("Starting testUpdateWarrantyNotFound");

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(WarrantyNotFoundException.class, () -> warrantyService.updateWarranty(warrantyDto.getWarrantyId(), warrantyDto));
        log.error("Warranty with given ID was not found");

        Mockito.verify(warrantyRepository).findById(warrantyDto.getWarrantyId());

        log.info("Finished testUpdateWarrantyNotFound successfully");
    }

    @Test
    public void testRemoveWarrantyByIdSuccess() {
        Warranty warranty = getDummyWarranty();

        log.info("Starting testRemoveWarrantyByIdSuccess");

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty));

        warrantyService.removeWarrantyById(warranty.getWarrantyId());
        log.info(String.valueOf(warranty.getWarrantyId()));

        Mockito.verify(warrantyRepository).findById(warranty.getWarrantyId());
        Mockito.verify(warrantyRepository).deleteById(warranty.getWarrantyId());

        log.info("Finished testRemoveWarrantyByIdSuccess successfully");
    }

    @Test
    public void testRemoveWarrantyByIdNotFound() {
        Warranty warranty = getDummyWarranty();

        log.info("Starting testRemoveWarrantyByIdNotFound");

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(WarrantyNotFoundException.class, () -> warrantyService.removeWarrantyById(warranty.getWarrantyId()));
        log.error("Warranty with given ID was not found");

        Mockito.verify(warrantyRepository).findById(warranty.getWarrantyId());

        log.info("Finished testRemoveWarrantyByIdNotFound successfully");
    }

    private Warranty getDummyWarranty(){
        Warranty warranty = new Warranty();
        warranty.setWarrantyId(10L);
        warranty.setDurationMonths(0);
        return warranty;
    }

    private WarrantyDto getDummyWarrantyDto(){
        WarrantyDto warrantyDto = new WarrantyDto();
        warrantyDto.setWarrantyId(10L);
        warrantyDto.setDurationMonths(0);
        return warrantyDto;
    }
}
