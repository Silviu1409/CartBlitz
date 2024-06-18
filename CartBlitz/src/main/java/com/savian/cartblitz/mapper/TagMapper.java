package com.savian.cartblitz.mapper;

import com.savian.cartblitz.dto.TagDto;
import com.savian.cartblitz.model.Tag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TagMapper {
    public Tag tagDtoToTag(TagDto tagDto){
        Tag tag = new Tag();
        tag.setTagId(tagDto.getTagId());
        tag.setName(tagDto.getName());
        return tag;
    }

    public List<Tag> tagDtosToTags(List<TagDto> tagDtos) {
        return tagDtos.stream()
                .map(this::tagDtoToTag)
                .collect(Collectors.toList());
    }

    public TagDto tagToTagDto(Tag tag){
        TagDto tagDto = new TagDto();
        tagDto.setTagId(tag.getTagId());
        tagDto.setName(tag.getName());
        return tagDto;
    }

    public List<TagDto> tagsToTagDtos(List<Tag> tags) {
        return tags.stream()
                .map(this::tagToTagDto)
                .collect(Collectors.toList());
    }
}
