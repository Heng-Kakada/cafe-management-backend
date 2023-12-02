package com.api.service;

import com.api.dto.request.ProductRequest;
import com.api.dto.response.PageResponse;
import com.api.dto.response.ResponseErrorTemplate;
import com.api.model.Product;

import java.util.Map;

public interface ProductService {
    ResponseErrorTemplate addProduct(ProductRequest productRequest);
    ResponseErrorTemplate updateProduct(Long id, ProductRequest productRequest);
    ResponseErrorTemplate getProduct(Long id);
    ResponseErrorTemplate updateProductStatus(Map<String, String> requestBody);
    ResponseErrorTemplate deleteProduct(Long id);
    ResponseErrorTemplate getProductByCategory(Long id);
    PageResponse getAllProducts(Map<String, String> param);
    Product getProductById(Long id);


}
