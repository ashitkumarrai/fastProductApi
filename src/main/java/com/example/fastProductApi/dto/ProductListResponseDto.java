package com.example.fastProductApi.dto;

import java.util.List;

public class ProductListResponseDto {
    private List<ProductResponseDto> products;
    private ResponseStatusVo responseStatus;


    public ProductListResponseDto() {
    }

    public ProductListResponseDto(List<ProductResponseDto> products) {
        this.products = products;
    }

    public ProductListResponseDto(List<ProductResponseDto> products, ResponseStatusVo responseStatus) {
        this.products = products;
        this.responseStatus = responseStatus;
    }

    @Override
    public String toString() {
        return "ProductListResponseDto{" +
                "products=" + products +
                '}';
    }

    public ResponseStatusVo getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatusVo responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<ProductResponseDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponseDto> products) {
        this.products = products;
    }
}

