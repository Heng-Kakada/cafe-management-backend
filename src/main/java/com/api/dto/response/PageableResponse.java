package com.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageableResponse {
    private int pageSize;
    private int pageNumber;
    private int totalPages;
    private long totalElements;
    private long numberOfElements;
    private boolean first;
    private boolean last;
    private boolean empty;
}
