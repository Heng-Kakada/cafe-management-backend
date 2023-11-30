package com.api.dto.response;


import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResponse {
    private List<?> list;
    private PageableResponse pageableDTO;
    public PageResponse(Page<?> page) {
        this.list = page.getContent();
        this.pageableDTO = PageableResponse.builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber() + 1)
                .totalElements(page.getTotalElements())
                .numberOfElements(page.getNumberOfElements())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
