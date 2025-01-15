package com.example.fastProductApi.service;

import static org.junit.jupiter.api.Assertions.*;
import com.example.fastProductApi.dto.*;
import com.example.fastProductApi.entity.Product;
import com.example.fastProductApi.exception.CustomException;
import com.example.fastProductApi.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import static org.mockito.Mockito.*;

class ProductServiceForBulkCrudTest {

    @Mock
    private ProductServiceForBasicCrud productServiceForBasicCrud;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceForBulkCrud productServiceForBulkCrud;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductByIdsInSeq() throws CustomException {
        List<Long> ids = List.of(1L, 2L);
        Product product1 = new Product();
        Product product2 = new Product();

        when(productServiceForBasicCrud.getProductById(1L)).thenReturn(Optional.of(product1));
        when(productServiceForBasicCrud.getProductById(2L)).thenReturn(Optional.of(product2));

        List<Optional<Product>> result = productServiceForBulkCrud.getProductByIdsInSeq(ids);

        assertEquals(2, result.size());
        assertTrue(result.get(0).isPresent());
        assertTrue(result.get(1).isPresent());
    }

    @Test
    void testGetProductByIdsInParallel() throws CustomException, ExecutionException, InterruptedException {
        List<Long> ids = List.of(1L, 2L);
        Product product1 = new Product();
        Product product2 = new Product();

        Future<Optional<Product>> future1 = mock(Future.class);
        Future<Optional<Product>> future2 = mock(Future.class);

        when(productServiceForBasicCrud.getProductById(1L)).thenReturn(Optional.of(product1));
        when(productServiceForBasicCrud.getProductById(2L)).thenReturn(Optional.of(product2));

        when(future1.get()).thenReturn(Optional.of(product1));
        when(future2.get()).thenReturn(Optional.of(product2));

        List<Optional<Product>> result = productServiceForBulkCrud.getProductByIdsInParallel(ids);

        assertEquals(2, result.size());
        assertTrue(result.get(0).isPresent());
        assertTrue(result.get(1).isPresent());
    }

    @Test
    void testSaveOrUpdateProductInParallel() throws CustomException {
        UploadProductListRequestDto requestDto = new UploadProductListRequestDto();
        ProductRequestDto productRequestDto = new ProductRequestDto(1l,"test", "test description", BigDecimal.valueOf(123),12);;
        requestDto.setProducts(List.of(productRequestDto));
        Product product = new Product();
        product.setId(1L);
        ProductResponseDto responseDto = new ProductResponseDto();
        when(productMapper.toEntity(productRequestDto)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(responseDto);
        when(productServiceForBasicCrud.saveProduct(product)).thenReturn(product);
        ProductListResponseDto result = productServiceForBulkCrud.saveOrUpdateProductInParallel(requestDto, false);
        assertEquals(1, result.getProducts().size());
        assertEquals("SUCCESS", result.getResponseStatus().message());
    }

    @Test
    void testDeleteByIdsInSeq() throws CustomException {
        List<Long> ids = List.of(1L, 2L);
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);

        when(productServiceForBasicCrud.getProductById(1L)).thenReturn(Optional.of(product1));
        when(productServiceForBasicCrud.getProductById(2L)).thenReturn(Optional.of(product2));

        ProductsDeleteResponseDto result = productServiceForBulkCrud.deleteByIdsInSeq(ids);

        assertEquals(2, result.getDeletedProductIds().size());
        assertTrue(result.getDeletedProductIds().containsAll(ids));
    }

    @Test
    void testDeleteByIdsInParallel() throws CustomException {
        List<Long> ids = List.of(1L, 2L);
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);

        when(productServiceForBasicCrud.getProductById(1L)).thenReturn(Optional.of(product1));
        when(productServiceForBasicCrud.getProductById(2L)).thenReturn(Optional.of(product2));

        ProductsDeleteResponseDto result = productServiceForBulkCrud.deleteByIdsInParallel(ids);

        assertEquals(2, result.getDeletedProductIds().size());
        assertTrue(result.getDeletedProductIds().containsAll(ids));
    }
}
