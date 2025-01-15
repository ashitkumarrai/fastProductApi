package com.example.fastProductApi.dto;

import java.util.List;

public class ProductsByIdResponseDto {
    private List<ProductResponseDto> product;
    private ResponseStatusVo responseStatus;

    public ProductsByIdResponseDto() {
    }

    public ProductsByIdResponseDto(List<ProductResponseDto> product, ResponseStatusVo responseStatus) {
        this.product = product;
        this.responseStatus = responseStatus;
    }

    @Override
    public String toString() {
        return "ProductsByIdResponseDto{" +
                "product=" + product +
                ", responseStatus=" + responseStatus +
                '}';
    }

    public List<ProductResponseDto> getProduct() {
        return product;
    }

    public void setProduct(List<ProductResponseDto> product) {
        this.product = product;
    }

    public ResponseStatusVo getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatusVo responseStatus) {
        this.responseStatus = responseStatus;
    }
}
