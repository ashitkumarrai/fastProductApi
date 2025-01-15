package com.example.fastProductApi.service;

import com.example.fastProductApi.entity.Product;
import com.example.fastProductApi.exception.CustomException;
import com.example.fastProductApi.repository.ProductRepository;
import com.example.fastProductApi.util.ConstantMessages;
import io.lettuce.core.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Retryable;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductServiceForBasicCrud {
    static Logger log = LoggerFactory.getLogger(ProductServiceForBasicCrud.class);
    @Autowired
    private ProductRepository productRepository;


    // Delete product and evict from cache
    @Retryable(value = {CustomException.class, RedisException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @CacheEvict(value = "products", key = "#product.id")
    public void deleteProduct(Product product) throws CustomException {
        try {
            productRepository.delete(product);
        } catch (Exception e) {
            log.error(ConstantMessages.EXCEPTION_LOGGER, e.getClass(), e.getMessage());
            throw new CustomException(e);
        }
    }


    // save product and put into cache
    @Retryable(value = {CustomException.class, RedisException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @CachePut(value = "products", key = "#result.id")
    public Product saveProduct(Product product) throws CustomException {
        try {
            if (product != null) {
                return productRepository.save(product);
            }
            return null;
        } catch (Exception e) {
            log.error(ConstantMessages.EXCEPTION_LOGGER, e.getClass(), e.getMessage());
            throw new CustomException(e);
        }
    }


    // update product and update into cache
    @Retryable(value = {CustomException.class, RedisException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @CachePut(value = "products", key = "#result.id")
    public Product updateProduct(Product product) throws CustomException {
        try {
            if (product != null && product.getId() == null) {
                // If product doesn't have an ID, it's a new product, so save it
                product.setCreatedAt(LocalDateTime.now());
                product.setLastUpdatedAt(LocalDateTime.now());
                return productRepository.save(product);
            } else {
                // If product has an ID, check if it exists
                Optional<Product> existingProduct = productRepository.findById(product.getId());

                // If product exists, update it; otherwise, return null
                if (existingProduct.isPresent()) {
                    Product productToUpdate = existingProduct.get();
                    productToUpdate.setName(product.getName());
                    productToUpdate.setPrice(product.getPrice());
                    productToUpdate.setStock(product.getStock());
                    productToUpdate.setDescription(product.getDescription());
                    productToUpdate.setLastUpdatedAt(LocalDateTime.now());

                    return productRepository.save(productToUpdate);  // Save updated product
                } else {
                    // Return null if the product is not found
                    return null;
                }
            }
        } catch (Exception e) {
            log.error(ConstantMessages.EXCEPTION_LOGGER, e.getClass(), e.getMessage());
            throw new CustomException(e);
        }
    }


    // get product by id from cache and in case of miss, query to db
    @Retryable(value = {CustomException.class, RedisException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @Cacheable(value = "products", key = "#id")
    public Optional<Product> getProductById(Long id) throws CustomException {
        try {
            Optional<Product> optionalProduct = productRepository.findById(id);
        } catch (Exception e) {
            log.error(ConstantMessages.EXCEPTION_LOGGER, e.getClass(), e.getMessage());
            throw new CustomException(e);
        }
        return Optional.empty();
    }
}
