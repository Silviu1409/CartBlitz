package com.savian.cartblitz.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savian.cartblitz.dto.WarrantyDto;
import com.savian.cartblitz.exception.WarrantyNotFoundException;
import com.savian.cartblitz.mapper.WarrantyMapper;
import com.savian.cartblitz.service.WarrantyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mysql")
@org.junit.jupiter.api.Tag("test")
public class WarrantyControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private WarrantyService warrantyService;
    @MockBean
    private WarrantyMapper warrantyMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllWarranties() throws Exception {
        List<WarrantyDto> warrantyDtoList = Arrays.asList(getDummyWarrantyDtoOne(), getDummyWarrantyDtoTwo());

        when(warrantyService.getAllWarranties()).thenReturn(warrantyDtoList);

        mockMvc.perform(get("/warranty")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.warrantyDtoList", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllWarrantiesAccessDenied() throws Exception {
        mockMvc.perform(get("/warranty")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetWarrantyById() throws Exception {
        WarrantyDto warrantyDto = getDummyWarrantyDtoOne();

        when(warrantyService.getWarrantyById(warrantyDto.getWarrantyId())).thenReturn(Optional.of(warrantyDto));

        mockMvc.perform(get("/warranty/id/{warrantyId}", warrantyDto.getWarrantyId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.warrantyId", is(warrantyDto.getWarrantyId().intValue())));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetWarrantyByIdAccessDenied() throws Exception {
        WarrantyDto warrantyDto = getDummyWarrantyDtoOne();

        mockMvc.perform(get("/warranty/id/{warrantyId}", warrantyDto.getWarrantyId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetWarrantyByIdNotFound() throws Exception {
        Long warrantyId = 99L;

        when(warrantyService.getWarrantyById(warrantyId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/warranty/id/{warrantyId}", warrantyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateWarranty() throws Exception {
        WarrantyDto warrantyDto = getDummyWarrantyDtoOne();

        when(warrantyService.saveWarranty(any())).thenReturn(warrantyDto);

        mockMvc.perform(post("/warranty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(warrantyDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/warranty/" + warrantyDto.getWarrantyId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateWarrantyInvalid() throws Exception {
        WarrantyDto warrantyDto = getDummyWarrantyDtoOne();
        warrantyDto.setDurationMonths(-1);

        when(warrantyService.saveWarranty(any())).thenReturn(warrantyDto);

        mockMvc.perform(post("/warranty")
                        .content(objectMapper.writeValueAsString(warrantyDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCreateWarrantyAccessDenied() throws Exception {
        WarrantyDto warrantyDto = getDummyWarrantyDtoOne();
        warrantyDto.setDurationMonths(-1);

        when(warrantyService.saveWarranty(any())).thenReturn(warrantyDto);

        mockMvc.perform(post("/warranty")
                        .content(objectMapper.writeValueAsString(warrantyDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateWarrantySuccess() throws Exception {
        WarrantyDto warrantyDto = getDummyWarrantyDtoOne();

        when(warrantyService.updateWarranty(anyLong(), any())).thenReturn(warrantyDto);

        mockMvc.perform(put("/warranty/id/{warrantyId}", warrantyDto.getWarrantyId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(warrantyDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warrantyId", is(warrantyDto.getWarrantyId().intValue())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateWarrantyInvalid() throws Exception {
        WarrantyDto warrantyDto = getDummyWarrantyDtoOne();
        warrantyDto.setDurationMonths(-1);

        when(warrantyService.updateWarranty(anyLong(), any())).thenReturn(warrantyDto);

        mockMvc.perform(put("/warranty/id/{warrantyId}", warrantyDto.getWarrantyId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(warrantyDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateWarrantyAccessDenied() throws Exception {
        WarrantyDto warrantyDto = getDummyWarrantyDtoOne();
        warrantyDto.setDurationMonths(-1);

        when(warrantyService.updateWarranty(anyLong(), any())).thenReturn(warrantyDto);

        mockMvc.perform(put("/warranty/id/{warrantyId}", warrantyDto.getWarrantyId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(warrantyDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteWarrantySuccess() throws Exception {
        Long warrantyId = 10L;

        mockMvc.perform(delete("/warranty/id/{warrantyId}", warrantyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteWarrantyNotFound() throws Exception {
        Long warrantyId = 10L;

        doThrow(new WarrantyNotFoundException(warrantyId)).when(warrantyService).removeWarrantyById(warrantyId);

        mockMvc.perform(delete("/warranty/id/{warrantyId}", warrantyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteWarrantyAccessDenied() throws Exception {
        Long warrantyId = 10L;

        doThrow(new WarrantyNotFoundException(warrantyId)).when(warrantyService).removeWarrantyById(warrantyId);

        mockMvc.perform(delete("/warranty/id/{warrantyId}", warrantyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private WarrantyDto getDummyWarrantyDtoOne(){
        WarrantyDto warrantyDto = new WarrantyDto();
        warrantyDto.setWarrantyId(10L);
        warrantyDto.setDurationMonths(0);
        warrantyDto.setType("type");
        warrantyDto.setTerms("terms");
        warrantyDto.setDetails("details");
        return warrantyDto;
    }

    private WarrantyDto getDummyWarrantyDtoTwo(){
        WarrantyDto warrantyDto = new WarrantyDto();
        warrantyDto.setWarrantyId(11L);
        warrantyDto.setDurationMonths(0);
        warrantyDto.setType("type");
        warrantyDto.setTerms("terms");
        warrantyDto.setDetails("details");
        return warrantyDto;
    }
}
