package com.acme.menusrv.dish.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DishDto {
    private UUID id;
    private String name;
    private String description;
    private String composition;
    private List<String> tags;
    private String primaryImage;
    private List<String> images;
    private boolean deleted;
}
