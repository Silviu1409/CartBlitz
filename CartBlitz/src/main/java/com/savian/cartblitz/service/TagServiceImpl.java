package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.TagDto;
import com.savian.cartblitz.exception.*;
import com.savian.cartblitz.mapper.TagMapper;
import com.savian.cartblitz.model.Tag;
import com.savian.cartblitz.repository.ProductRepository;
import com.savian.cartblitz.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService{
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;
    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository, ProductRepository productRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.productRepository = productRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream().map(tagMapper::tagToTagDto).toList();
    }

    @Override
    public Optional<TagDto> getTagById(Long tagId) {
        Optional<TagDto> tagDto = tagRepository.findById(tagId).map(tagMapper::tagToTagDto);
        
        if (tagDto.isPresent()) {
            return tagDto;
        }
        else {
            throw new TagNotFoundException(tagId);
        }
    }

    @Override
    public Optional<TagDto> getTagByName(String name) {
        Optional<TagDto> tagDto = tagRepository.findByName(name).map(tagMapper::tagToTagDto);

        if (tagDto.isPresent()) {
            return tagDto;
        }
        else {
            throw new TagNotFoundException(name);
        }
    }

    @Override
    public List<TagDto> getTagsByProductId(Long productId) {
        productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

        return tagRepository.findByProductsProductId(productId).stream().map(tagMapper::tagToTagDto).toList();
    }

    @Override
    public TagDto saveTag(TagDto tagDto) {
        Optional<Tag> existingTag = tagRepository.findByName(tagDto.getName());

        if (existingTag.isPresent()) {
            throw new TagNameDuplicateException();
        } else {
            tagRepository.save(tagMapper.tagDtoToTag(tagDto));
            
            return tagDto;
        }
    }

    @Override
    public TagDto updateTag(Long tagId, TagDto tagDto) {
        Optional<Tag> optTag = tagRepository.findById(tagId);
        
        if (optTag.isPresent()){
            Optional<Tag> duplicateTag = tagRepository.findByName(tagDto.getName());
            if (duplicateTag.isPresent() && !Objects.equals(duplicateTag.get().getTagId(), tagId)) {
                throw new TagNameDuplicateException();
            }

            Tag existingTag = optTag.get();

            existingTag.setName(tagDto.getName());
            tagRepository.save(existingTag);

            return tagDto;
        }
        else{
            throw new TagNotFoundException(tagId);
        }
    }

    @Override
    public void removeTagById(Long tagId) {
        Optional<Tag> tag = tagRepository.findById(tagId);
        
        if(tag.isPresent()){
            tagRepository.deleteById(tagId);
        }
        else{
            throw new TagNotFoundException(tagId);
        }
    }
}
