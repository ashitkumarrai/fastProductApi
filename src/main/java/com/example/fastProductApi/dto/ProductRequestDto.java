package com.example.fastProductApi.dto;

import java.math.BigDecimal;

public record ProductRequestDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stock
) {
}