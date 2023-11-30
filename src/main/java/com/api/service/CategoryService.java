package com.api.service;

import com.api.dto.request.CategoryRequest;
import com.api.dto.response.PageResponse;
import com.api.dto.response.ResponseErrorTemplate;
import com.api.model.Category;

import java.util.Map;

public interface CategoryService {
    ResponseErrorTemplate addCategory(CategoryRequest request);
    PageResponse getAllCategories(Map<String, String> params);
    ResponseErrorTemplate getCategory(Long id);
    ResponseErrorTemplate updateCategory(Long id, CategoryRequest categoryRequest);
    ResponseErrorTemplate deleteCategory(Long id);
    Category getCategoryById(Long id);
}
