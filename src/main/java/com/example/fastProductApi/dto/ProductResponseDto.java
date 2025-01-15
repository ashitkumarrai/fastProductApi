package com.example.fastProductApi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponseDto {

        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private int stock;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastUpdatedAt;

        public ProductResponseDto() {
        }

        public ProductResponseDto(Long id, String name, String description, BigDecimal price, int stock, LocalDateTime createdAt, LocalDateTime lastUpdatedAt, ResponseStatusVo responseStatus) {
                this.id = id;
                this.name = name;
                this.description = description;
                this.price = price;
                this.stock = stock;
                this.createdAt = createdAt;
                this.lastUpdatedAt = lastUpdatedAt;
        }

        @Override
        public String toString() {
                return "ProductResponseDTO{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        ", description='" + description + '\'' +
                        ", price=" + price +
                        ", stock=" + stock +
                        ", createdAt=" + createdAt +
                        ", lastUpdatedAt=" + lastUpdatedAt +
                        '}';
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public BigDecimal getPrice() {
                return price;
        }

        public void setPrice(BigDecimal price) {
                this.price = price;
        }

        public int getStock() {
                return stock;
        }

        public void setStock(int stock) {
                this.stock = stock;
        }

        public LocalDateTime getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
        }

        public LocalDateTime getLastUpdatedAt() {
                return lastUpdatedAt;
        }

        public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
                this.lastUpdatedAt = lastUpdatedAt;
        }

}