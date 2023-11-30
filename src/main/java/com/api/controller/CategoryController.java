package com.api.controller;


import com.api.dto.request.CategoryRequest;
import com.api.dto.response.PageResponse;
import com.api.dto.response.ResponseErrorTemplate;
import com.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @GetMapping()
    public ResponseEntity<?> showAllCategories(@RequestParam Map<String, String> params){
        PageResponse response = categoryService.getAllCategories(params);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("{id}")
    public ResponseEntity<?> getCategory(@PathVariable("id") Long id){
        ResponseErrorTemplate response = categoryService.getCategory(id);
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") Long id, @RequestBody CategoryRequest categoryRequest){
        ResponseErrorTemplate response = categoryService.updateCategory(id, categoryRequest);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id){
        ResponseErrorTemplate response = categoryService.deleteCategory(id);
        return ResponseEntity.ok().body(response);
    }

}
