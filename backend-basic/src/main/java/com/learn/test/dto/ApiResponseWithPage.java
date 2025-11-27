package com.learn.test.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiResponseWithPage<T> {
    private boolean success;
    private String message;
    private List<T> data;
    private int page;
    private int size;
    private int totalPages;

    public static <T> ApiResponseWithPage<T> success(List<T> data, int page, int size, int totalPages) {
        return ApiResponseWithPage.<T>builder()
                .success(true)
                .message(null)
                .data(data)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }

    public static <T> ApiResponseWithPage<T> success(List<T> data, int page, int size, int totalPages, String message) {
        return ApiResponseWithPage.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }

    public static <T> ApiResponseWithPage<T> error(String message) {
        return ApiResponseWithPage.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .page(0)
                .size(0)
                .totalPages(0)
                .build();
    }
}

