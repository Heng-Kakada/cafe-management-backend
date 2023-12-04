package com.api.service.impl;

import com.api.dto.request.CategoryRequest;
import com.api.dto.response.PageResponse;
import com.api.dto.response.ResponseErrorTemplate;
import com.api.exception.BaseException;
import com.api.model.Category;
import com.api.repository.CategoryRepository;
import com.api.service.CategoryService;
import com.api.utils.ConstantUtils;
import com.api.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public ResponseErrorTemplate addCategory(CategoryRequest request) {
        this.validateCategory(request);
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        categoryRepository.save(category);
        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_OK)
                .message("Successfully Created...")
                .data(category)
                .build();
    }
    @Override
    public PageResponse getAllCategories(Map<String, String> params) {
        int pageLimit = PageUtils.DEFAULT_PAGE_LIMIT;
        if (params.containsKey(PageUtils.PAGE_LIMIT)){
            pageLimit = Integer.parseInt(params.get(PageUtils.PAGE_LIMIT));
        }

        int pageNumber = PageUtils.DEFAULT_PAGE_NUMBER;
        if (params.containsKey(PageUtils.PAGE_NUMBER)){
            pageNumber = Integer.parseInt(params.get(PageUtils.PAGE_NUMBER));
        }
        Pageable pageable = PageUtils.getPageable(pageNumber, pageLimit);
        Page<Category> pages = categoryRepository.findAll(pageable);
        return new PageResponse(pages);
    }
    @Override
    public ResponseErrorTemplate getCategory(Long id) {
        Category category = getCategoryById(id);

        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_OK)
                .message("Success!")
                .data(category)
                .build();
    }
    @Override
    public ResponseErrorTemplate updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = getCategoryById(id);

        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        category = categoryRepository.save(category);

        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_OK)
                .message("success updating")
                .data(category)
                .build();
    }
    @Override
    public ResponseErrorTemplate deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_OK)
                .message("success deleting")
                .data(category)
                .build();
    }
    @Override
    public Category getCategoryById(Long id){
        return categoryRepository.findById(id).orElseThrow(
                () -> new BaseException(ConstantUtils.SC_BD, "Category %d not found!".formatted(id))
        );
    }
    private void validateCategory(CategoryRequest request){
        var category = categoryRepository.findByName(request.getName());
        if (Objects.isNull(request.getName())  || request.getName().isEmpty() || request.getName().isBlank()  ){
            throw new BaseException(ConstantUtils.SC_BD, "Name : must be not null!");
        }
        if (Objects.nonNull(category)){
            throw new BaseException(ConstantUtils.SC_BD, "%s : is already created !".formatted(request.getName()));
        }
    }
}
