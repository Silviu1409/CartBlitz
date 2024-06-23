package com.savian.cartblitz.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savian.cartblitz.dto.TagDto;
import com.savian.cartblitz.exception.TagNotFoundException;
import com.savian.cartblitz.mapper.TagMapper;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.model.Tag;
import com.savian.cartblitz.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mysql")
@org.junit.jupiter.api.Tag("test")
public class TagControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private TagService tagService;
    
    @MockBean
    private TagMapper tagMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllTagsSuccess() throws Exception {
        List<TagDto> dummyTags = List.of(getDummyTagDtoOne(), getDummyTagDtoTwo());
        when(tagService.getAllTags()).thenReturn(dummyTags);

        mockMvc.perform(get("/tag")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.tagDtoList", hasSize(dummyTags.size())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTagByIdSuccess() throws Exception {
        Long tagId = 10L;
        Tag tag = getDummyTagOne();
        TagDto tagDto = getDummyTagDtoOne();

        when(tagService.getTagById(tagId)).thenReturn(Optional.of(tagDto));

        mockMvc.perform(get("/tag/id/{tagId}", tagId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(tag.getName())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTagByIdNotFound() throws Exception {
        Long tagId = 10L;

        when(tagService.getTagById(tagId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tag/id/{tagId}", tagId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTagByNameSuccess() throws Exception {
        String tagName = "name";
        Tag tag = getDummyTagOne();
        TagDto tagDto = getDummyTagDtoOne();

        when(tagService.getTagByName(tagName)).thenReturn(Optional.of(tagDto));

        mockMvc.perform(get("/tag/name/{tagName}", tagName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(tag.getName())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTagByNameNotFound() throws Exception {
        String tagName = "name";

        when(tagService.getTagByName(tagName)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tag/name/{tagName}", tagName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetTagsByProductIdSuccess() throws Exception {
        TagDto tagOne = getDummyTagDtoOne();
        Tag tag = getDummyTagOne();
        Product product = getDummyProduct();
        tag.setProducts(Collections.singletonList(product));

        when(tagService.getTagsByProductId(tag.getProducts().get(0).getProductId())).thenReturn(List.of(tagOne));

        mockMvc.perform(get("/tag/product/{productId}", tag.getProducts().get(0).getProductId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.tagDtoList[0].tagId").value(tag.getTagId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateTagSuccess() throws Exception {
        Tag tag = getDummyTagOne();
        TagDto tagDto = getDummyTagDtoOne();

        when(tagService.saveTag(any())).thenReturn(tagDto);

        mockMvc.perform(post("/tag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/tag/" + tag.getTagId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateTagInvalid() throws Exception {
        TagDto tagDto = getDummyTagDtoOne();
        tagDto.setName("");

        mockMvc.perform(post("/tag")
                        .content(objectMapper.writeValueAsString(tagDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateTagAccessDenied() throws Exception {
        TagDto tagDto = getDummyTagDtoOne();
        tagDto.setName("");

        mockMvc.perform(post("/tag")
                        .content(objectMapper.writeValueAsString(tagDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTagSuccess() throws Exception {
        TagDto tagDto = getDummyTagDtoOne();
        Tag tag = getDummyTagOne();

        when(tagService.updateTag(anyLong(), any())).thenReturn(tagDto);

        mockMvc.perform(put("/tag/id/{tagId}", tag.getTagId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId", is(tag.getTagId().intValue())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTagInvalid() throws Exception {
        Long tagId = 10L;
        TagDto tagDto = getDummyTagDtoOne();
        tagDto.setName("");

        mockMvc.perform(put("/tag/id/{tagId}", tagId)
                        .content(objectMapper.writeValueAsString(tagDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateTagAccessDenied() throws Exception {
        Long tagId = 10L;
        TagDto tagDto = getDummyTagDtoOne();
        tagDto.setName("");

        mockMvc.perform(put("/tag/id/{tagId}", tagId)
                        .content(objectMapper.writeValueAsString(tagDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteTagSuccess() throws Exception {
        Long tagId = 10L;

        mockMvc.perform(delete("/tag/id/{tagId}", tagId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteTagNotFound() throws Exception {
        Long tagId = 10L;

        doThrow(new TagNotFoundException(tagId)).when(tagService).removeTagById(tagId);

        mockMvc.perform(delete("/tag/id/{tagId}", tagId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteTagAccessDenied() throws Exception {
        Long tagId = 10L;

        doThrow(new TagNotFoundException(tagId)).when(tagService).removeTagById(tagId);

        mockMvc.perform(delete("/tag/id/{tagId}", tagId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private Tag getDummyTagOne(){
        Tag tag = new Tag();
        tag.setTagId(10L);
        tag.setName("name");
        return tag;
    }

    private TagDto getDummyTagDtoOne(){
        TagDto tagDto = new TagDto();
        tagDto.setTagId(10L);
        tagDto.setName("name");
        return tagDto;
    }

    private TagDto getDummyTagDtoTwo(){
        TagDto tagDto = new TagDto();
        tagDto.setTagId(10L);
        tagDto.setName("name");
        return tagDto;
    }

    private Product getDummyProduct(){
        Product product = new Product();
        product.setProductId(10L);
        product.setName("productTest");
        product.setPrice(BigDecimal.valueOf(0L));
        product.setStockQuantity(0);
        product.setDescription("productTest description");
        product.setBrand("productTest brand");
        product.setCategory("productTest category");
        return product;
    }
}
