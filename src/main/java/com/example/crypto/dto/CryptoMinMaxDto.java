package com.example.crypto.dto;

import java.math.BigDecimal;

public record CryptoMinMaxDto(String symbol, BigDecimal minPrice, BigDecimal maxPrice) {}
