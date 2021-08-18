package com.acme.commons.openapi;

import lombok.Data;

import java.util.List;

@Data
public abstract class OpenApiPage<T> {
    private int number;
    private long totalElements;
    private int totalPages;
    private int size;
    private List<T> content;
}
