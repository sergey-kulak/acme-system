package com.acme.menusrv.menu.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class MenuCategoryDto {
    private UUID id;
    private String name;
}
