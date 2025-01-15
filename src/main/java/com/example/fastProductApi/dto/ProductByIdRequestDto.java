package com.example.fastProductApi.dto;


import java.util.List;

public record ProductByIdRequestDto(
        List<Long> ids
) {
}
