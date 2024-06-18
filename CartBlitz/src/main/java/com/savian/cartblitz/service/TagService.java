package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.TagDto;

import java.util.List;
import java.util.Optional;

public interface TagService {
    List<TagDto> getAllTags();
    Optional<TagDto> getTagById(Long tagId);
    Optional<TagDto> getTagByName(String name);
    
    List<TagDto> getTagsByProductId(Long productId);

    TagDto saveTag(TagDto tagDto);
    TagDto updateTag(Long tagId, TagDto tagDto);
    void removeTagById(Long tagId);
}
