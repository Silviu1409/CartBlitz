package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.WarrantyDto;
import com.savian.cartblitz.exception.WarrantyNotFoundException;
import com.savian.cartblitz.mapper.WarrantyMapper;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.repository.WarrantyRepository;
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

        Mockito.when(warrantyRepository.findAll()).thenReturn(warranties);

        List<WarrantyDto> result = warrantyService.getAllWarranties();

        Mockito.verify(warrantyRepository).findAll();
        Assertions.assertEquals(warranties.stream().map(warrantyMapper::warrantyToWarrantyDto).toList(), result);
    }

    @Test
    public void testGetWarrantyByIdFound() {
        Warranty warranty = getDummyWarranty();
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty));
        Mockito.when(warrantyMapper.warrantyToWarrantyDto(Mockito.any(Warranty.class))).thenReturn(warrantyDto);

        Optional<WarrantyDto> result = warrantyService.getWarrantyById(warranty.getWarrantyId());

        Mockito.verify(warrantyRepository).findById(warranty.getWarrantyId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(warrantyDto, result.get());
    }

    @Test
    public void testGetWarrantyByIdNotFound() {
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(WarrantyNotFoundException.class, () -> warrantyService.getWarrantyById(warrantyDto.getWarrantyId()));

        Mockito.verify(warrantyRepository).findById(warrantyDto.getWarrantyId());
    }

    @Test
    void testSaveWarrantySuccess() {
        Warranty warranty = getDummyWarranty();
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        Mockito.when(warrantyRepository.save(Mockito.any())).thenReturn(warranty);
        Mockito.when(warrantyMapper.warrantyToWarrantyDto(Mockito.any())).thenReturn(warrantyDto);

        WarrantyDto result = warrantyService.saveWarranty(warrantyDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(warrantyDto.getWarrantyId(), result.getWarrantyId());
    }

    @Test
    void testUpdateWarrantySuccess() {
        Warranty existingWarranty = getDummyWarranty();
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        Mockito.when(warrantyRepository.findById(Mockito.any())).thenReturn(Optional.of(existingWarranty));
        Mockito.when(warrantyRepository.save(Mockito.any())).thenReturn(existingWarranty);
        Mockito.when(warrantyMapper.warrantyToWarrantyDto(Mockito.any())).thenReturn(warrantyDto);

        WarrantyDto result = warrantyService.updateWarranty(existingWarranty.getWarrantyId(), warrantyDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(warrantyDto.getWarrantyId(), result.getWarrantyId());
    }

    @Test
    public void testUpdateWarrantyNotFound() {
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(WarrantyNotFoundException.class, () -> warrantyService.updateWarranty(warrantyDto.getWarrantyId(), warrantyDto));

        Mockito.verify(warrantyRepository).findById(warrantyDto.getWarrantyId());
    }

    @Test
    public void testRemoveWarrantyByIdSuccess() {
        Warranty warranty = getDummyWarranty();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty));

        warrantyService.removeWarrantyById(warranty.getWarrantyId());

        Mockito.verify(warrantyRepository).findById(warranty.getWarrantyId());
        Mockito.verify(warrantyRepository).deleteById(warranty.getWarrantyId());
    }

    @Test
    public void testRemoveWarrantyByIdNotFound() {
        Warranty warranty = getDummyWarranty();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(WarrantyNotFoundException.class, () -> warrantyService.removeWarrantyById(warranty.getWarrantyId()));

        Mockito.verify(warrantyRepository).findById(warranty.getWarrantyId());
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
