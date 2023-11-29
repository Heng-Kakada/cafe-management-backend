package com.api.service;

import com.api.dto.request.CategoryRequest;
import com.api.dto.response.ResponseErrorTemplate;

public interface CategoryService {
    ResponseErrorTemplate addCategory(CategoryRequest request);


}
