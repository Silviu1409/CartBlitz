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
        Mockito.when(tagRepository.findAll()).thenReturn(tags);

        List<TagDto> result = tagService.getAllTags();

        Mockito.verify(tagRepository).findAll();
        Assertions.assertEquals(tags.stream().map(tagMapper::tagToTagDto).toList(), result);
    }

    @Test
    public void testGetTagByIdFound() {
        Tag tag = getDummyTag();
        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));

        TagDto tagDto = getDummyTagDto();
        Mockito.when(tagMapper.tagToTagDto(tag)).thenReturn(tagDto);

        Optional<TagDto> result = tagService.getTagById(tag.getTagId());

        Mockito.verify(tagRepository).findById(tag.getTagId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(tagDto, result.get());
    }

    @Test
    public void testGetTagByIdNotFound() {
        Tag tag = getDummyTag();

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(TagNotFoundException.class, () -> tagService.getTagById(tag.getTagId()));

        Mockito.verify(tagRepository).findById(tag.getTagId());
    }

    @Test
    public void testGetTagByNameFound() {
        Tag tag = getDummyTag();
        Mockito.when(tagRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(tag));

        TagDto tagDto = getDummyTagDto();
        Mockito.when(tagMapper.tagToTagDto(tag)).thenReturn(tagDto);

        Optional<TagDto> result = tagService.getTagByName(tag.getName());

        Mockito.verify(tagRepository).findByName(tag.getName());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(tagDto, result.get());
    }

    @Test
    public void testGetTagByNameNotFound() {
        Tag tag = getDummyTag();

        Mockito.when(tagRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(TagNotFoundException.class, () -> tagService.getTagByName(tag.getName()));

        Mockito.verify(tagRepository).findByName(tag.getName());
    }

    @Test
    public void testGetTagsByProductIdNotFound(){
        Tag tag = getDummyTag();
        Product product = getDummyProduct();
        tag.setProducts(Collections.singletonList(product));

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> tagService.getTagsByProductId(product.getProductId()));

        Mockito.verify(tagRepository, Mockito.never()).findByProductsProductId(Mockito.anyLong());
    }

    @Test
    public void testGetProductsByTagFound(){
        List<Tag> tags = new ArrayList<>();
        Tag tag = getDummyTag();
        Product product = getDummyProduct();
        tag.setProducts(Collections.singletonList(product));
        tags.add(tag);

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(product));
        Mockito.when(tagRepository.findByProductsProductId(Mockito.anyLong())).thenReturn(tags);

        List<TagDto> result = tagService.getTagsByProductId(product.getProductId());

        Mockito.verify(tagRepository).findByProductsProductId(product.getProductId());
        Assertions.assertEquals(tags.stream().map(tagMapper::tagToTagDto).toList(), result);
    }

    @Test
    public void testSaveTagSuccess() {
        TagDto tagDto = getDummyTagDto();
        Tag savedTag = getDummyTag();
        TagDto savedTagDto = tagMapper.tagToTagDto(savedTag);

        Mockito.when(tagRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(tagRepository.save(tagMapper.tagDtoToTag(Mockito.any(TagDto.class)))).thenReturn(savedTag);

        TagDto result = tagService.saveTag(tagDto);

        Mockito.verify(tagRepository).findByName(tagDto.getName());
        Mockito.verify(tagRepository).save(tagMapper.tagDtoToTag(tagDto));
        Assertions.assertEquals(savedTagDto, result);
    }

    @Test
    public void testSaveTagDuplicate() {
        Tag existingTag = getDummyTag();
        Mockito.when(tagRepository.findByName(existingTag.getName())).thenReturn(Optional.of(existingTag));

        TagDto tagDto = getDummyTagDto();

        Assertions.assertThrows(TagNameDuplicateException.class, () -> tagService.saveTag(tagDto));

        Mockito.verify(tagRepository, Mockito.never()).save(Mockito.any(Tag.class));
        Mockito.verify(tagMapper, Mockito.never()).tagDtoToTag(Mockito.any(TagDto.class));
    }

//    @Test
//    public void testUpdateTagSuccess() {
//        Tag existingTag = getDummyTag();
//        TagDto tagDto = getDummyTagDto();
//        Tag updatedTag = getDummyTag();
//        TagDto updatedTagDto = getDummyTagDto();
//
//        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingTag));
//        Mockito.when(tagRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
//        Mockito.when(tagRepository.save(Mockito.any(Tag.class))).thenReturn(updatedTag);
//        Mockito.when(tagMapper.tagDtoToTag(Mockito.any(TagDto.class))).thenReturn(Mockito.any(Tag.class));
//
//        TagDto result = tagService.updateTag(existingTag.getTagId(), tagDto);
//
//        Mockito.verify(tagRepository).findById(tagDto.getTagId());
//        Mockito.verify(tagRepository).findByName(tagDto.getName());
//        Mockito.verify(tagRepository).save(existingTag);
//        Mockito.verify(tagMapper).tagToTagDto(updatedTag);
//        Assertions.assertEquals(updatedTagDto, result);
//    }

    @Test
    public void testUpdateTagNotFound() {
        TagDto tagDto = getDummyTagDto();

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(TagNotFoundException.class, () -> tagService.updateTag(tagDto.getTagId(), tagDto));

        Mockito.verify(tagRepository).findById(tagDto.getTagId());
    }

    @Test
    public void testUpdateTagDuplicate() {
        Tag existingTag = getDummyTag();
        Mockito.when(tagRepository.findByName(existingTag.getName())).thenReturn(Optional.of(existingTag));

        TagDto tagDto = getDummyTagDto();

        Assertions.assertThrows(TagNameDuplicateException.class, () -> tagService.saveTag(tagDto));

        Mockito.verify(tagRepository, Mockito.never()).save(Mockito.any(Tag.class));
        Mockito.verify(tagMapper, Mockito.never()).tagDtoToTag(Mockito.any(TagDto.class));
    }

    @Test
    public void testRemoveTagByIdSuccess() {
        Tag tag = getDummyTag();

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));

        tagService.removeTagById(tag.getTagId());

        Mockito.verify(tagRepository).findById(tag.getTagId());
        Mockito.verify(tagRepository).deleteById(tag.getTagId());
    }

    @Test
    public void testRemoveTagByIdNotFound() {
        Tag tag = getDummyTag();

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(TagNotFoundException.class, () -> tagService.removeTagById(tag.getTagId()));

        Mockito.verify(tagRepository).findById(tag.getTagId());
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
