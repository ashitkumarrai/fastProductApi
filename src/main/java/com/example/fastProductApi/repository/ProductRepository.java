package com.example.fastProductApi.repository;

import com.example.fastProductApi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
