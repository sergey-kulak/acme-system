package com.acme.usersrv.common.openapi;

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
