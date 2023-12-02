package com.api.service.impl;

import com.api.dto.request.ProductRequest;
import com.api.dto.response.PageResponse;
import com.api.dto.response.ResponseErrorTemplate;
import com.api.exception.BaseException;
import com.api.model.Category;
import com.api.model.Product;
import com.api.repository.ProductRepository;
import com.api.service.CategoryService;
import com.api.service.ProductService;
import com.api.utils.ConstantUtils;
import com.api.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Override
    public ResponseErrorTemplate addProduct(ProductRequest productRequest) {
        existProductByNameAndSize(productRequest.getName(), productRequest.getSize());

        Category category = categoryService.getCategoryById(productRequest.getCategoryId());

        Product product = Product.builder()
                .name(productRequest.getName())
                .size(productRequest.getSize())
                .price(productRequest.getPrice())
                .status(ConstantUtils.ACT)
                .category(category)
                .build();

        product = productRepository.save(product);

        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_OK)
                .message("Success Adding")
                .data(product)
                .build();
    }
    @Override
    public ResponseErrorTemplate updateProduct(Long id,ProductRequest productRequest) {

        if ( Objects.isNull( productRequest.getCategoryId() ) ){
            throw new BaseException(ConstantUtils.SC_BD, "id must not be null");
        }

        Product product = getProductById(id);
        Category category = categoryService.getCategoryById(productRequest.getCategoryId());

        product.setName(productRequest.getName());
        product.setSize(productRequest.getSize());
        product.setPrice(productRequest.getPrice());
        product.setStatus(ConstantUtils.ACT);
        product.setCategory(category);

        product = productRepository.save(product);

        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_OK)
                .message("Success updating")
                .data(product)
                .build();
    }
    @Override
    public ResponseErrorTemplate getProduct(Long id) {

        Product product = getProductById(id);

        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_OK)
                .message("success")
                .data(product)
                .build();
    }
    @Override
    public ResponseErrorTemplate updateProductStatus(Map<String, String> requestBody) {
        if (requestBody.containsKey("status") && requestBody.containsKey("id")){
            Product product = getProductById( Long.parseLong(requestBody.get("id")));
            productRepository.updateProductStatus(Long.parseLong(requestBody.get("id")), requestBody.get("status"));
            return ResponseErrorTemplate.builder()
                    .code(ConstantUtils.SC_OK)
                    .message("Success update status")
                    .build();
        }
        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_BD)
                .message("something went wrong!")
                .build();
    }
    @Override
    public ResponseErrorTemplate deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_OK)
                .message("success deleting")
                .data(product)
                .build();
    }
    @Override
    public ResponseErrorTemplate getProductByCategory(Long id) {
        List<Product> products = productRepository.findByCategoryId(id);
        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_OK)
                .message("success")
                .data(products)
                .build();
    }
    @Override
    public PageResponse getAllProducts(Map<String, String> params) {

        int pageLimit = PageUtils.DEFAULT_PAGE_LIMIT;
        if (params.containsKey(PageUtils.PAGE_LIMIT)){
            pageLimit = Integer.parseInt(params.get(PageUtils.PAGE_LIMIT));
        }

        int pageNumber = PageUtils.DEFAULT_PAGE_NUMBER;
        if (params.containsKey(PageUtils.PAGE_NUMBER)){
            pageNumber = Integer.parseInt(params.get(PageUtils.PAGE_NUMBER));
        }

        Pageable pageable = PageUtils.getPageable(pageNumber, pageLimit);
        Page<Product> pages = productRepository.findAll(pageable);

        return new PageResponse(pages);
    }
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new BaseException(ConstantUtils.SC_BD, "Product id = %d not found!".formatted(id))
        );
    }
    private void existProductByNameAndSize(String name, String size){
        Product product = productRepository.findByNameAndSize(name, size);
        if (Objects.nonNull(product)){
            throw new BaseException(ConstantUtils.SC_BD, "%s and %s : is already exist".formatted(name, size));
        }

    }
}
