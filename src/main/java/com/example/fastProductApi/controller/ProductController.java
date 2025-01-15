package com.example.fastProductApi.controller;

import com.example.fastProductApi.dto.*;
import com.example.fastProductApi.entity.Product;
import com.example.fastProductApi.mapper.ProductMapper;
import com.example.fastProductApi.service.ProductServiceForBasicCrud;
import com.example.fastProductApi.service.ProductServiceForBulkCrud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductServiceForBulkCrud productServiceForBulkCrud;

    @Autowired
    private ProductServiceForBasicCrud productServiceForBasicCrud;

    @Autowired
    private ProductMapper productMapper;

    /**
     * Fetch products by IDs with an option for sequential or parallel processing.
     * @param isParallel Indicates if the operation should use parallel processing.
     * @param productByIdRequestDto Contains the list of product IDs to fetch.
     * @return ResponseEntity containing found products and any missing IDs.
     */
    @PostMapping("/getProductByIds")
    public ResponseEntity<ProductsByIdResponseDto> getProductById(
            @RequestParam(defaultValue = "true") boolean isParallel,
            @RequestBody ProductByIdRequestDto productByIdRequestDto) {

        List<Optional<Product>> products = fetchProductsByIds(productByIdRequestDto.ids(), isParallel);
        return ResponseEntity.ok(mapProductsToResponse(products, productByIdRequestDto.ids()));
    }

    /**
     * Upload a list of products. Supports both sequential and parallel processing.
     * @param isParallel Indicates if the operation should use parallel processing.
     * @param uploadProductListRequestDto Contains the list of products to upload.
     * @return ResponseEntity with details of uploaded products.
     */
    @PostMapping("/uploadProducts")
    public ResponseEntity<ProductListResponseDto> uploadProductList(
            @RequestParam(defaultValue = "true") boolean isParallel,
            @RequestBody UploadProductListRequestDto uploadProductListRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(processProductList(uploadProductListRequestDto, isParallel, false));
    }

    /**
     * Update a list of products. Supports both sequential and parallel processing.
     * @param isParallel Indicates if the operation should use parallel processing.
     * @param uploadProductListRequestDto Contains the list of products to update.
     * @return ResponseEntity with details of updated products.
     */
    @PutMapping("/updateProductsByIds")
    public ResponseEntity<ProductListResponseDto> updateProductList(
            @RequestParam(defaultValue = "true") boolean isParallel,
            @RequestBody UploadProductListRequestDto uploadProductListRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(processProductList(uploadProductListRequestDto, isParallel, true));
    }

    /**
     * Delete products by IDs with an option for sequential or parallel processing.
     * @param isParallel Indicates if the operation should use parallel processing.
     * @param ids Contains the list of product IDs to delete.
     * @return ResponseEntity with details of deleted products.
     */
    @DeleteMapping("/deleteProductsByIds")
    public ResponseEntity<ProductsDeleteResponseDto> deleteProducts(
            @RequestParam(defaultValue = "true") boolean isParallel,
            @RequestBody ProductByIdRequestDto ids) {

        ProductsDeleteResponseDto response = isParallel
                ? productServiceForBulkCrud.deleteByIdsInParallel(ids.ids())
                : productServiceForBulkCrud.deleteByIdsInSeq(ids.ids());

        return ResponseEntity.ok(response);
    }

    /**
     * Fetches products based on IDs using either sequential or parallel processing.
     */
    private List<Optional<Product>> fetchProductsByIds(List<Long> ids, boolean isParallel) {
        return isParallel
                ? productServiceForBulkCrud.getProductByIdsInParallel(ids)
                : productServiceForBulkCrud.getProductByIdsInSeq(ids);
    }

    /**
     * Maps a list of products (some optional) to a response DTO, identifying missing products.
     */
    private ProductsByIdResponseDto mapProductsToResponse(List<Optional<Product>> products, List<Long> ids) {
        List<String> notFoundIds = new ArrayList<>();
        List<ProductResponseDto> productDTOs = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            Optional<Product> optionalProduct = products.get(i);
            if (optionalProduct.isPresent()) {
                productDTOs.add(productMapper.toDTO(optionalProduct.get()));
            } else {
                notFoundIds.add(ids.get(i).toString());
            }
        }

        if (notFoundIds.isEmpty()) {
            return new ProductsByIdResponseDto(productDTOs,
                    new ResponseStatusVo("SUCCESS", HttpStatus.OK.toString()));
        } else {
            String errorMessage = "Products not found for the following IDs: " + String.join(", ", notFoundIds);
            return new ProductsByIdResponseDto(productDTOs,
                    new ResponseStatusVo(errorMessage, HttpStatus.PARTIAL_CONTENT.toString()));
        }
    }

    /**
     * Handles both product saving and updating with an option for sequential or parallel processing.
     */
    private ProductListResponseDto processProductList(UploadProductListRequestDto dto, boolean isParallel, boolean isUpdate) {
        return isParallel
                ? productServiceForBulkCrud.saveOrUpdateProductInParallel(dto, isUpdate)
                : processProductsSequentially(dto.getProducts(), isUpdate);
    }

    /**
     * Processes products sequentially for saving or updating.
     */
    private ProductListResponseDto processProductsSequentially(List<ProductRequestDto> productRequestDTOs, boolean isUpdate) {
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for (ProductRequestDto requestDto : productRequestDTOs) {
            Product product = productMapper.toEntity(requestDto);
            product.setLastUpdatedAt(LocalDateTime.now());
            if (!isUpdate) {
                product.setCreatedAt(LocalDateTime.now());
            }
            Product processedProduct = isUpdate
                    ? productServiceForBasicCrud.updateProduct(product)
                    : productServiceForBasicCrud.saveProduct(product);
            productResponseDtos.add(productMapper.toDTO(processedProduct));
        }
        return new ProductListResponseDto(productResponseDtos);
    }
}
