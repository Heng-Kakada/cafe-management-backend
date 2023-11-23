package com.api.service.impl;

import com.api.dto.request.CategoryRequest;
import com.api.dto.response.ResponseErrorTemplate;
import com.api.exception.BaseException;
import com.api.model.Category;
import com.api.repository.CategoryRepository;
import com.api.service.CategoryService;
import com.api.utils.ConstantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private void validateCategory(CategoryRequest request){

        var category = categoryRepository.findByName(request.getName());

        if ( Objects.isNull(request.getName())  || request.getName().isEmpty() || request.getName().isBlank()  ){
            throw new BaseException(ConstantUtils.SC_BD, "Name : must be not null!");
        }

        if (Objects.nonNull(category)){
            throw new BaseException(ConstantUtils.SC_BD, "%s : is already created !".formatted(request.getName()));
        }

    }
}
