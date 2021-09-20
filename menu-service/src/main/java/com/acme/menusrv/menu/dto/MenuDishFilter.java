package com.acme.menusrv.menu.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class MenuDishFilter {
    private UUID categoryId;
    private String searchText;
    private Set<String> tags;
}
