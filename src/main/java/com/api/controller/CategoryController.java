package com.api.controller;


import com.api.dto.request.CategoryRequest;
import com.api.dto.response.ResponseErrorTemplate;
import com.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequest categoryRequest){
        ResponseErrorTemplate response = categoryService.addCategory(categoryRequest);
        return ResponseEntity.ok().body(response);
    }


}
