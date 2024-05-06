package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.TagDto;
import com.savian.cartblitz.exception.ProductNotFoundException;
import com.savian.cartblitz.exception.TagNameDuplicateException;
import com.savian.cartblitz.exception.TagNotFoundException;
import com.savian.cartblitz.mapper.TagMapper;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.model.Tag;
import com.savian.cartblitz.repository.ProductRepository;
import com.savian.cartblitz.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("h2")
@Slf4j
public class TagServiceUnitTest {
    @InjectMocks
    private TagServiceImpl tagService;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private TagMapper tagMapper;

    @Test
    public void testGetAllTags() {
        List<Tag> tags = new ArrayList<>();
        tags.add(getDummyTag());

        log.info("Starting testGetAllTags");

        Mockito.when(tagRepository.findAll()).thenReturn(tags);

        List<TagDto> result = tagService.getAllTags();
        tags.forEach(tag -> log.info(String.valueOf(tag.getTagId())));

        Mockito.verify(tagRepository).findAll();
        Assertions.assertEquals(tags.stream().map(tagMapper::tagToTagDto).toList(), result);

        log.info("Finished testGetAllTags successfully");
    }

    @Test
    public void testGetTagByIdFound() {
        Tag tag = getDummyTag();

        log.info("Starting testGetTagByIdFound");

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));

        TagDto tagDto = getDummyTagDto();
        Mockito.when(tagMapper.tagToTagDto(tag)).thenReturn(tagDto);

        Optional<TagDto> result = tagService.getTagById(tag.getTagId());
        result.ifPresent(value -> log.info(String.valueOf(value.getTagId())));

        Mockito.verify(tagRepository).findById(tag.getTagId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(tagDto, result.get());

        log.info("Finished testGetTagByIdFound successfully");
    }

    @Test
    public void testGetTagByIdNotFound() {
        Tag tag = getDummyTag();

        log.info("Starting testGetTagByIdNotFound");

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(TagNotFoundException.class, () -> tagService.getTagById(tag.getTagId()));
        log.error("Tag with given ID was not found");

        Mockito.verify(tagRepository).findById(tag.getTagId());

        log.info("Finished testGetTagByIdNotFound successfully");
    }

    @Test
    public void testGetTagByNameFound() {
        Tag tag = getDummyTag();

        log.info("Starting testGetTagByNameFound");

        Mockito.when(tagRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(tag));

        TagDto tagDto = getDummyTagDto();

        Mockito.when(tagMapper.tagToTagDto(tag)).thenReturn(tagDto);

        Optional<TagDto> result = tagService.getTagByName(tag.getName());
        result.ifPresent(value -> log.info(String.valueOf(value.getTagId())));

        Mockito.verify(tagRepository).findByName(tag.getName());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(tagDto, result.get());

        log.info("Finished testGetTagByNameFound successfully");
    }

    @Test
    public void testGetTagByNameNotFound() {
        Tag tag = getDummyTag();

        log.info("Starting testGetTagByNameNotFound");

        Mockito.when(tagRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(TagNotFoundException.class, () -> tagService.getTagByName(tag.getName()));
        log.error("Tag with given ID was not found");

        Mockito.verify(tagRepository).findByName(tag.getName());

        log.info("Finished testGetTagByNameNotFound successfully");
    }

    @Test
    public void testGetTagsByProductIdNotFound(){
        Tag tag = getDummyTag();
        Product product = getDummyProduct();
        tag.setProducts(Collections.singletonList(product));

        log.info("Starting testGetTagsByProductIdNotFound");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> tagService.getTagsByProductId(product.getProductId()));
        log.error("Product with given ID was not found");

        Mockito.verify(tagRepository, Mockito.never()).findByProductsProductId(Mockito.anyLong());

        log.info("Finished testGetTagsByProductIdNotFound successfully");
    }

    @Test
    public void testGetProductsByTagFound(){
        List<Tag> tags = new ArrayList<>();
        Tag tag = getDummyTag();
        Product product = getDummyProduct();
        tag.setProducts(Collections.singletonList(product));
        tags.add(tag);

        log.info("Starting testGetProductsByTagFound");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(product));
        Mockito.when(tagRepository.findByProductsProductId(Mockito.anyLong())).thenReturn(tags);

        List<TagDto> result = tagService.getTagsByProductId(product.getProductId());
        tags.forEach(tag1 -> log.info(String.valueOf(tag1.getTagId())));

        Mockito.verify(tagRepository).findByProductsProductId(product.getProductId());
        Assertions.assertEquals(tags.stream().map(tagMapper::tagToTagDto).toList(), result);

        log.info("Finished testGetProductsByTagFound successfully");
    }

    @Test
    public void testSaveTagSuccess() {
        TagDto tagDto = getDummyTagDto();
        Tag savedTag = getDummyTag();

        log.info("Starting testSaveTagSuccess");

        Mockito.when(tagRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(tagRepository.save(tagMapper.tagDtoToTag(Mockito.any(TagDto.class)))).thenReturn(savedTag);

        TagDto result = tagService.saveTag(tagDto);
        log.info(String.valueOf(tagDto.getTagId()));

        Mockito.verify(tagRepository).findByName(tagDto.getName());
        Mockito.verify(tagRepository).save(tagMapper.tagDtoToTag(tagDto));
        Assertions.assertEquals(tagDto, result);

        log.info("Finished testSaveTagSuccess successfully");
    }

    @Test
    public void testSaveTagDuplicate() {
        Tag existingTag = getDummyTag();

        log.info("Starting testSaveTagDuplicate");

        Mockito.when(tagRepository.findByName(existingTag.getName())).thenReturn(Optional.of(existingTag));

        TagDto tagDto = getDummyTagDto();

        Assertions.assertThrows(TagNameDuplicateException.class, () -> tagService.saveTag(tagDto));
        log.error("A tag with the same name already exists");

        Mockito.verify(tagRepository, Mockito.never()).save(Mockito.any(Tag.class));
        Mockito.verify(tagMapper, Mockito.never()).tagDtoToTag(Mockito.any(TagDto.class));

        log.info("Finished testSaveTagDuplicate successfully");
    }

    @Test
    public void testUpdateTagSuccess() {
        Tag existingTag = getDummyTag();
        TagDto tagDto = getDummyTagDto();

        log.info("Starting testUpdateTagSuccess");

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingTag));
        Mockito.when(tagRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(tagRepository.save(Mockito.any(Tag.class))).thenReturn(Mockito.any(Tag.class));

        TagDto result = tagService.updateTag(existingTag.getTagId(), tagDto);
        log.info(String.valueOf(tagDto.getTagId()));

        Mockito.verify(tagRepository).findById(tagDto.getTagId());
        Mockito.verify(tagRepository).findByName(tagDto.getName());
        Mockito.verify(tagRepository).save(existingTag);
        Assertions.assertEquals(tagDto, result);

        log.info("Finished testUpdateTagSuccess successfully");
    }

    @Test
    public void testUpdateTagNotFound() {
        TagDto tagDto = getDummyTagDto();

        log.info("Starting testUpdateTagNotFound");

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(TagNotFoundException.class, () -> tagService.updateTag(tagDto.getTagId(), tagDto));
        log.error("Tag with given ID was not found");

        Mockito.verify(tagRepository).findById(tagDto.getTagId());

        log.info("Finished testUpdateTagNotFound successfully");
    }

    @Test
    public void testUpdateTagDuplicate() {
        Tag existingTag = getDummyTag();

        log.info("Starting testUpdateTagDuplicate");

        Mockito.when(tagRepository.findByName(existingTag.getName())).thenReturn(Optional.of(existingTag));

        TagDto tagDto = getDummyTagDto();

        Assertions.assertThrows(TagNameDuplicateException.class, () -> tagService.saveTag(tagDto));
        log.error("A tag with the same name already exists");

        Mockito.verify(tagRepository, Mockito.never()).save(Mockito.any(Tag.class));
        Mockito.verify(tagMapper, Mockito.never()).tagDtoToTag(Mockito.any(TagDto.class));

        log.info("Finished testUpdateTagDuplicate successfully");
    }

    @Test
    public void testRemoveTagByIdSuccess() {
        Tag tag = getDummyTag();

        log.info("Starting testRemoveTagByIdSuccess");

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));

        tagService.removeTagById(tag.getTagId());
        log.info(String.valueOf(tag.getTagId()));

        Mockito.verify(tagRepository).findById(tag.getTagId());
        Mockito.verify(tagRepository).deleteById(tag.getTagId());

        log.info("Finished testRemoveTagByIdSuccess successfully");
    }

    @Test
    public void testRemoveTagByIdNotFound() {
        Tag tag = getDummyTag();

        log.info("Starting testRemoveTagByIdNotFound");

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(TagNotFoundException.class, () -> tagService.removeTagById(tag.getTagId()));
        log.error("Tag with given ID was not found");

        Mockito.verify(tagRepository).findById(tag.getTagId());

        log.info("Finished testRemoveTagByIdNotFound successfully");
    }

    private Tag getDummyTag(){
        Tag tag = new Tag();
        tag.setTagId(10L);
        tag.setName("name");
        return tag;
    }

    private TagDto getDummyTagDto(){
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
