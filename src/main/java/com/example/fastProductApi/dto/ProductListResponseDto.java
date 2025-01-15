package com.example.fastProductApi.dto;

import java.util.List;

public class ProductListResponseDto {
    private List<ProductResponseDto> products;


    public ProductListResponseDto() {
    }

    public ProductListResponseDto(List<ProductResponseDto> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "ProductListResponseDto{" +
                "products=" + products +
                '}';
    }

    public List<ProductResponseDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponseDto> products) {
        this.products = products;
    }
}

