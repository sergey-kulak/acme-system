package com.acme.menusrv.menu.mapper;

import com.acme.menusrv.menu.Category;
import com.acme.menusrv.menu.dto.CategoryDto;
import com.acme.menusrv.menu.dto.CreateCategoryDto;
import com.acme.menusrv.menu.dto.UpdateCategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(source = "id", target = "id")
    Category fromDto(CreateCategoryDto dto, UUID id);

    CategoryDto toDto(Category s);

    void update(@MappingTarget Category category, UpdateCategoryDto source);
}
