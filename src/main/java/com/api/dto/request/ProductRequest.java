package com.api.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductRequest {
    private String name;
    private String size;
    private Double price;
    private Long categoryId;
}
