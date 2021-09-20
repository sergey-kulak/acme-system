package com.acme.menusrv.dish.mapper;

import com.acme.menusrv.dish.Dish;
import com.acme.menusrv.dish.dto.CreateDishDto;
import com.acme.menusrv.dish.dto.DishDto;
import com.acme.menusrv.dish.dto.FullDetailsDishDto;
import com.acme.menusrv.dish.dto.UpdateDishDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DishMapper {
    DishDto toDto(Dish s);

    FullDetailsDishDto toFullDetailsDto(Dish s);

    Dish fromDto(CreateDishDto s);

    void update(@MappingTarget Dish target, UpdateDishDto source);
}
