package com.example.fastProductApi.service;

import com.example.fastProductApi.dto.*;
import com.example.fastProductApi.entity.Product;
import com.example.fastProductApi.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceForBulkCrud {
    @Autowired
    private ProductServiceForBasicCrud productServiceForBasicCrud;

    @Autowired
    private ProductMapper productMapper;

    public static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * Fetch products by IDs sequentially.
     *
     * @param ids List of product IDs to fetch.
     * @return List of Optional<Product> containing the fetched products.
     */
    public List<Optional<Product>> getProductByIdsInSeq(List<Long> ids) {
        return ids.stream()
                .map(productServiceForBasicCrud::getProductById)
                .collect(Collectors.toList());
    }

    /**
     * Fetch products by IDs in parallel using ExecutorService.
     *
     * @param ids List of product IDs to fetch.
     * @return List of Optional<Product> containing the fetched products.
     */
    public List<Optional<Product>> getProductByIdsInParallel(List<Long> ids) {
        // Submit tasks for each product ID and collect futures
        List<Future<Optional<Product>>> futures = ids.stream()
                .map(id -> executorService.submit(() -> productServiceForBasicCrud.getProductById(id)))
                .collect(Collectors.toList());

        // Retrieve results from futures
        return getFutureResults(futures, Optional.empty());
    }

    /**
     * Save or update products in parallel.
     *
     * @param uploadProductListRequestDto Request DTO containing product data.
     * @param isUpdate                    Flag to determine save or update operation.
     * @return ProductListResponseDto containing the operation results.
     */
    public ProductListResponseDto saveOrUpdateProductInParallel(
            UploadProductListRequestDto uploadProductListRequestDto, boolean isUpdate) {

        List<ProductResponseDto> productResponseDtos = Collections.synchronizedList(new ArrayList<>());

        // Submit tasks for each product request
        List<Future<Object>> futures = uploadProductListRequestDto.getProducts().stream()
                .map(productRequestDto -> executorService.submit(() -> {
                    processProduct(productRequestDto, isUpdate, productResponseDtos);
                    return null;
                }))
                .collect(Collectors.toList());

        // Wait for all tasks to complete
        waitForFutures(futures);

        // Construct and return response
        ProductListResponseDto responseDto = new ProductListResponseDto();
        responseDto.setProducts(productResponseDtos);
        return responseDto;
    }

    /**
     * Delete products by IDs sequentially.
     *
     * @param ids List of product IDs to delete.
     * @return ProductsDeleteResponseDto containing the operation results.
     */
    public ProductsDeleteResponseDto deleteByIdsInSeq(List<Long> ids) {
        return deleteByIds(ids, false);
    }

    /**
     * Delete products by IDs in parallel.
     *
     * @param ids List of product IDs to delete.
     * @return ProductsDeleteResponseDto containing the operation results.
     */
    public ProductsDeleteResponseDto deleteByIdsInParallel(List<Long> ids) {
        return deleteByIds(ids, true);
    }

    // === Helper Methods ===

    /**
     * Generalized method to delete products by IDs.
     *
     * @param ids        List of product IDs.
     * @param isParallel Flag to determine parallel or sequential execution.
     * @return ProductsDeleteResponseDto containing the operation results.
     */
    private ProductsDeleteResponseDto deleteByIds(List<Long> ids, boolean isParallel) {
        List<Long> deletedProductIds = new ArrayList<>();
        List<String> notDeletedProductIds = new ArrayList<>();

        if (isParallel) {
            // Parallel execution using ExecutorService
            List<Future<Object>> futures = ids.stream()
                    .map(id -> executorService.submit(() -> {
                        processDeletion(id, deletedProductIds, notDeletedProductIds);
                        return null;
                    }))
                    .collect(Collectors.toList());

            // Wait for all parallel tasks to complete
            waitForFutures(futures);
        } else {
            // Sequential execution
            ids.forEach(id -> processDeletion(id, deletedProductIds, notDeletedProductIds));
        }

        // Prepare response DTO
        ProductsDeleteResponseDto responseDto = new ProductsDeleteResponseDto();
        responseDto.setDeletedProductIds(deletedProductIds);
        responseDto.setResponseStatus(
                notDeletedProductIds.isEmpty()
                        ? new ResponseStatusVo("SUCCESS", HttpStatus.OK.toString())
                        : new ResponseStatusVo(
                        "Products not found for the following IDs to delete: " + String.join(", ", notDeletedProductIds),
                        HttpStatus.PARTIAL_CONTENT.toString()
                ));
        return responseDto;
    }


    /**
     * Helper method to process product save or update.
     *
     * @param productRequestDto   Product request DTO.
     * @param isUpdate            Flag for update or save operation.
     * @param productResponseDtos List to collect response DTOs.
     */
    private void processProduct(ProductRequestDto productRequestDto, boolean isUpdate, List<ProductResponseDto> productResponseDtos) {
        Product product = productMapper.toEntity(productRequestDto);

        if (isUpdate) {
            Product updatedProduct = productServiceForBasicCrud.updateProduct(product);
            productResponseDtos.add(productMapper.toDTO(updatedProduct));
        } else {
            product.setCreatedAt(LocalDateTime.now());
            product.setLastUpdatedAt(LocalDateTime.now());
            Product savedProduct = productServiceForBasicCrud.saveProduct(product);
            productResponseDtos.add(productMapper.toDTO(savedProduct));
        }


    }

    /**
     * Helper method to process product deletion.
     *
     * @param id                   Product ID to delete.
     * @param deletedProductIds    List to collect deleted product IDs.
     * @param notDeletedProductIds List to collect IDs of products not found.
     */
    private void processDeletion(Long id, List<Long> deletedProductIds, List<String> notDeletedProductIds) {
        Optional<Product> product = productServiceForBasicCrud.getProductById(id);
        if (product.isPresent()) {
            productServiceForBasicCrud.deleteProduct(id);
            deletedProductIds.add(id);
        } else {
            notDeletedProductIds.add(id.toString());
        }
    }

    /**
     * Helper method to retrieve results from futures.
     *
     * @param futures      List of futures.
     * @param defaultValue Default value in case of exceptions.
     * @return List of results.
     */
    private <T> List<T> getFutureResults(List<Future<T>> futures, T defaultValue) {
        return futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        Thread.currentThread().interrupt();
                        return defaultValue;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Helper method to wait for completion of futures.
     *
     * @param futures List of futures.
     */
    private void waitForFutures(List<Future<Object>> futures) {
        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                // Log or handle exception
            }
        });
    }
}
