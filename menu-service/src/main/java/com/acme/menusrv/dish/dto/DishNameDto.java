package com.acme.menusrv.dish.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DishNameDto {
    private UUID id;
    private String name;
}
