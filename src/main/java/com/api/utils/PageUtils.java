package com.api.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PageUtils {
    int DEFAULT_PAGE_LIMIT = 2;
    int DEFAULT_PAGE_NUMBER = 1;
    String PAGE_LIMIT = "_limit";
    String PAGE_NUMBER = "_page";
    static Pageable getPageable(int pageNumber, int pageSize) {
        if (pageSize < DEFAULT_PAGE_LIMIT) {
            pageSize = DEFAULT_PAGE_LIMIT;
        }
        if (pageNumber < DEFAULT_PAGE_NUMBER) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        Pageable page = PageRequest.of(pageNumber - 1, pageSize); // - 1 cause programming start with 0
        return page;
    }

}
