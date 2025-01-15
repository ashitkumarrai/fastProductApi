package com.example.fastProductApi.dto;

import java.util.List;

public class ProductsDeleteResponseDto {
    private List<Long> deletedProductIds;
    private ResponseStatusVo responseStatus;

    public ProductsDeleteResponseDto(List<Long> deletedProductIds, ResponseStatusVo responseStatus) {
        this.deletedProductIds = deletedProductIds;
        this.responseStatus = responseStatus;
    }

    public ProductsDeleteResponseDto() {
    }

    @Override
    public String toString() {
        return "ProductsDeleteResponseDto{" +
                "deletedProductIds=" + deletedProductIds +
                ", responseStatus=" + responseStatus +
                '}';
    }

    public List<Long> getDeletedProductIds() {
        return deletedProductIds;
    }

    public void setDeletedProductIds(List<Long> deletedProductIds) {
        this.deletedProductIds = deletedProductIds;
    }

    public ResponseStatusVo getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatusVo responseStatus) {
        this.responseStatus = responseStatus;
    }
}
