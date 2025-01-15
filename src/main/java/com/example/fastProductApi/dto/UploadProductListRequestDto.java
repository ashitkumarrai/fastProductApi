package com.example.fastProductApi.dto;

import java.util.List;

public class UploadProductListRequestDto {
    private List<ProductRequestDto> products;



    public UploadProductListRequestDto() {
    }

    public UploadProductListRequestDto(List<ProductRequestDto> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "UploadProductListRequestDto{" +
                "products=" + products +
                '}';
    }

    public List<ProductRequestDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductRequestDto> products) {
        this.products = products;
    }
}
