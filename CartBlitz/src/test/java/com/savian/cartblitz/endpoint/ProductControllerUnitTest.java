package com.savian.cartblitz.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savian.cartblitz.dto.ProductDto;
import com.savian.cartblitz.dto.TagDto;
import com.savian.cartblitz.exception.ProductNotFoundException;
import com.savian.cartblitz.mapper.ProductMapper;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mysql")
@org.junit.jupiter.api.Tag("test")
public class ProductControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductService productService;
    @MockBean
    private ProductMapper productMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllProducts() throws Exception {
        List<ProductDto> productDtoList = Arrays.asList(getDummyProductDtoOne(), getDummyProductDtoTwo());

        when(productService.getAllProducts()).thenReturn(productDtoList);

        mockMvc.perform(get("/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.productDtoList.size()", is(productDtoList.size())))
                .andExpect(jsonPath("$._embedded.productDtoList[0].name", is(productDtoList.get(0).getName())))
                .andExpect(jsonPath("$._embedded.productDtoList[1].name", is(productDtoList.get(1).getName())));
    }

    @Test
    void testGetProductByIdSuccess() throws Exception {
        Product product = getDummyProductOne();

        when(productService.getProductById(product.getProductId())).thenReturn(Optional.of(product));

        mockMvc.perform(MockMvcRequestBuilders.get("/product/id/{productId}", product.getProductId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("product"));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        Long productId = 10L;

        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/product/id/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error"));
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        String category = "CPU";
        List<ProductDto> productDtoList = Arrays.asList(getDummyProductDtoOne(), getDummyProductDtoTwo());

        when(productService.getProductsByCategory(category)).thenReturn(productDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/product/category/{category}", category)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("products"));
    }

    @Test
    public void testSortProductsByCategoryRedirect() throws Exception {
        String category = "electronics";
        String sortBy = "name";
        String sortOrder = "asc";
        List<ProductDto> products = List.of(getDummyProductDtoOne(), getDummyProductDtoTwo());
        when(productService.sortProducts(products, sortBy, sortOrder)).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders.get("/product/category/{category}/sort", category)
                        .param("sortBy", sortBy)
                        .param("sortOrder", sortOrder)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetProductsByBrand() throws Exception {
        String brand = "Intel";
        List<ProductDto> productDtoList = Arrays.asList(getDummyProductDtoOne(), getDummyProductDtoTwo());

        when(productService.getProductsByBrand(brand)).thenReturn(productDtoList);

        mockMvc.perform(get("/product/brand/{brand}", brand)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.productDtoList.size()", is(productDtoList.size())))
                .andExpect(jsonPath("$._embedded.productDtoList[0].name", is(productDtoList.get(0).getName())))
                .andExpect(jsonPath("$._embedded.productDtoList[1].name", is(productDtoList.get(1).getName())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetProductsByPriceRange() throws Exception {
        BigDecimal minPrice = BigDecimal.valueOf(50.0);
        BigDecimal maxPrice = BigDecimal.valueOf(100.0);
        List<ProductDto> productDtoList = Arrays.asList(getDummyProductDtoOne(), getDummyProductDtoTwo());

        when(productService.getProductsByPriceRange(minPrice, maxPrice)).thenReturn(productDtoList);

        mockMvc.perform(get("/product/priceRange")
                        .param("minPrice", String.valueOf(minPrice))
                        .param("maxPrice", String.valueOf(maxPrice))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.productDtoList.size()", is(productDtoList.size())))
                .andExpect(jsonPath("$._embedded.productDtoList[0].name", is(productDtoList.get(0).getName())))
                .andExpect(jsonPath("$._embedded.productDtoList[1].name", is(productDtoList.get(1).getName())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetProductsByTagIdSuccess() throws Exception {
        ProductDto productOne = getDummyProductDtoOne();
        TagDto tagDto = getDummyTagDto();
        productOne.setTags(Collections.singletonList(tagDto));

        when(productService.getProductsByTagId(productOne.getTags().get(0).getTagId())).thenReturn(List.of(productOne));

        mockMvc.perform(get("/product/tag/{tagId}", productOne.getTags().get(0).getTagId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.productDtoList[0].productId").value(productOne.getProductId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStockQuantity() throws Exception {
        Long productId = 10L;
        Integer stockQuantity = 50;
        Product product = getDummyProductOne();

        when(productService.updateStockQuantity(productId, stockQuantity)).thenReturn(product);

        mockMvc.perform(patch("/product")
                        .param("productId", String.valueOf(productId))
                        .param("stockQuantity", String.valueOf(stockQuantity))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stockQuantity", is(product.getStockQuantity())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateStockQuantityAccessDenied() throws Exception {
        Long productId = 10L;
        Integer stockQuantity = 50;
        Product product = getDummyProductOne();

        when(productService.updateStockQuantity(productId, stockQuantity)).thenReturn(product);

        mockMvc.perform(patch("/product")
                        .param("productId", String.valueOf(productId))
                        .param("stockQuantity", String.valueOf(stockQuantity))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProductSuccess() throws Exception {
        Product product = getDummyProductOne();
        ProductDto productDto = getDummyProductDtoOne();

        when(productService.saveProduct(any())).thenReturn(product);

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/product/" + productDto.getProductId()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateProductAccessDenied() throws Exception {
        ProductDto productDto = getDummyProductDtoOne();

        mockMvc.perform(post("/product")
                        .content(objectMapper.writeValueAsString(productDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProductInvalid() throws Exception {
        ProductDto productDto = getDummyProductDtoOne();
        productDto.setStockQuantity(-1);

        mockMvc.perform(post("/product")
                        .content(objectMapper.writeValueAsString(productDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProductSuccess() throws Exception {
        ProductDto productDto = getDummyProductDtoOne();
        Product product = getDummyProductOne();

        when(productService.updateProduct(anyLong(), any())).thenReturn(product);

        mockMvc.perform(put("/product/id/{productId}", productDto.getProductId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(productDto.getProductId().intValue())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProductInvalid() throws Exception {
        Long productId = 10L;
        ProductDto productDto = getDummyProductDtoOne();
        productDto.setStockQuantity(-1);

        mockMvc.perform(put("/product/id/{productId}", productId)
                        .content(objectMapper.writeValueAsString(productDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteProductSuccess() throws Exception {
        Long productId = 10L;

        mockMvc.perform(delete("/product/id/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteProductNotFound() throws Exception {
        Long productId = 10L;

        doThrow(new ProductNotFoundException(productId)).when(productService).removeProductById(productId);

        mockMvc.perform(delete("/product/id/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteProductAccessDenied() throws Exception {
        mockMvc.perform(delete("/product/id/{productId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private Product getDummyProductOne(){
        Product product = new Product();
        product.setProductId(10L);
        product.setName("productTest");
        product.setPrice(BigDecimal.valueOf(1L));
        product.setStockQuantity(0);
        product.setDescription("productTest description");
        product.setBrand("productTest brand");
        product.setCategory("productTest category");
        return product;
    }

    private ProductDto getDummyProductDtoOne(){
        ProductDto productDto = new ProductDto();
        productDto.setProductId(10L);
        productDto.setName("productTest");
        productDto.setPrice(BigDecimal.valueOf(1L));
        productDto.setStockQuantity(0);
        productDto.setDescription("productTest description");
        productDto.setBrand("productTest brand");
        productDto.setCategory("productTest category");
        return productDto;
    }

    private ProductDto getDummyProductDtoTwo(){
        ProductDto productDto = new ProductDto();
        productDto.setProductId(10L);
        productDto.setName("productTest");
        productDto.setPrice(BigDecimal.valueOf(1L));
        productDto.setStockQuantity(0);
        productDto.setDescription("productTest description");
        productDto.setBrand("productTest brand");
        productDto.setCategory("productTest category");
        return productDto;
    }

    private TagDto getDummyTagDto(){
        TagDto tagDto = new TagDto();
        tagDto.setTagId(10L);
        tagDto.setName("name");
        return tagDto;
    }
}
