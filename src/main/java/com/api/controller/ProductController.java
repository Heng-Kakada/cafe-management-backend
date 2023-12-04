package com.api.controller;


import com.api.dto.request.ProductRequest;
import com.api.dto.response.PageResponse;
import com.api.dto.response.ResponseErrorTemplate;
import com.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody ProductRequest productRequest){
        ResponseErrorTemplate response = productService.addProduct(productRequest);
        return ResponseEntity.ok().body(response);
    }
    /**
     * go to localhost:8080/api/products/{id}
     */
    @PutMapping("{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long id,@RequestBody ProductRequest productRequest){
        ResponseErrorTemplate response = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("/updateStatus")
    public ResponseEntity<?> updateProductStatus(@RequestBody Map<String,String> requestBody){
        ResponseErrorTemplate response = productService.updateProductStatus(requestBody);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id){
        ResponseErrorTemplate response = productService.deleteProduct(id);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("{id}")
    public ResponseEntity<?> getProduct(@PathVariable("id") Long id){
        ResponseErrorTemplate response = productService.getProduct(id);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/getByCategory/{id}")
    public ResponseEntity<?> getProductByCategory(@PathVariable("id") Long id){
        ResponseErrorTemplate response = productService.getProductByCategory(id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping()
    public ResponseEntity<?> getAllProducts(@RequestParam Map<String, String> params){
        PageResponse response = productService.getAllProducts(params);
        return ResponseEntity.ok().body(response);
    }

}
