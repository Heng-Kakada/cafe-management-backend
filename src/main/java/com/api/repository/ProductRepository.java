package com.api.repository;

import com.api.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);
    Product findByNameAndSize(String name, String size);
    @Query("UPDATE Product SET status=:status WHERE id = :id ")
    @Modifying
    @Transactional
    void updateProductStatus(@Param("id") Long id, @Param("status") String status);
    List<Product> findByCategoryId(Long id);
}
