package com.example.fastProductApi.controller;

import com.example.fastProductApi.dto.*;
import com.example.fastProductApi.entity.Product;
import com.example.fastProductApi.exception.CustomException;
import com.example.fastProductApi.mapper.ProductMapper;
import com.example.fastProductApi.service.ProductServiceForBasicCrud;
import com.example.fastProductApi.service.ProductServiceForBulkCrud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductServiceForBulkCrud productServiceForBulkCrud;

    @Mock
    private ProductServiceForBasicCrud productServiceForBasicCrud;

    @Mock
    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductById_success() throws CustomException {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        List<Optional<Product>> products = List.of(Optional.of(new Product()), Optional.of(new Product()));
        ProductByIdRequestDto requestDto = new ProductByIdRequestDto(ids);

        when(productServiceForBulkCrud.getProductByIdsInParallel(ids)).thenReturn(products);

        ProductsByIdResponseDto expectedResponse = new ProductsByIdResponseDto(
                new ArrayList<>(),
                new ResponseStatusVo("SUCCESS", "200")
        );

        // Act
        ResponseEntity<ProductsByIdResponseDto> response = productController.getProductById(true, requestDto);

        // Assert
        assertEquals(expectedResponse.getResponseStatus().message(), response.getBody().getResponseStatus().message());
        verify(productServiceForBulkCrud).getProductByIdsInParallel(ids);
    }

    @Test
    void getProductById_exception() throws CustomException {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        ProductByIdRequestDto requestDto = new ProductByIdRequestDto(ids);

        when(productServiceForBulkCrud.getProductByIdsInParallel(ids)).thenThrow(new CustomException(new Exception("Test Exception")));

        // Act
        ResponseEntity<ProductsByIdResponseDto> response = productController.getProductById(true, requestDto);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        verify(productServiceForBulkCrud).getProductByIdsInParallel(ids);
    }

    @Test
    void uploadProductList_success() throws CustomException {
        // Arrange
        UploadProductListRequestDto requestDto = new UploadProductListRequestDto(new ArrayList<>());

        ProductListResponseDto expectedResponse = new ProductListResponseDto(
                new ArrayList<>(),
                new ResponseStatusVo("SUCCESS", "201")
        );

        when(productServiceForBulkCrud.saveOrUpdateProductInParallel(requestDto, false))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ProductListResponseDto> response = productController.uploadProductList(true, requestDto);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        verify(productServiceForBulkCrud).saveOrUpdateProductInParallel(requestDto, false);
    }

    @Test
    void updateProductList_success() throws CustomException {
        // Arrange
        UploadProductListRequestDto requestDto = new UploadProductListRequestDto(new ArrayList<>());

        ProductListResponseDto expectedResponse = new ProductListResponseDto(
                new ArrayList<>(),
                new ResponseStatusVo("SUCCESS", "201")
        );

        when(productServiceForBulkCrud.saveOrUpdateProductInParallel(requestDto, true))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ProductListResponseDto> response = productController.updateProductList(true, requestDto);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        verify(productServiceForBulkCrud).saveOrUpdateProductInParallel(requestDto, true);
    }

    @Test
    void deleteProducts_success() throws CustomException {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        ProductByIdRequestDto requestDto = new ProductByIdRequestDto(ids);

        ProductsDeleteResponseDto expectedResponse = new ProductsDeleteResponseDto(
                new ArrayList<>(),
                new ResponseStatusVo("SUCCESS", "200")
        );

        when(productServiceForBulkCrud.deleteByIdsInParallel(ids)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ProductsDeleteResponseDto> response = productController.deleteProducts(true, requestDto);

        // Assert
        assertEquals("200 OK", response.getStatusCode().toString());
        verify(productServiceForBulkCrud).deleteByIdsInParallel(ids);
    }
}
