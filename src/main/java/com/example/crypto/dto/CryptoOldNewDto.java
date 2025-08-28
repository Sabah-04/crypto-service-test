package com.example.crypto.dto;



import java.math.BigDecimal;

public record CryptoOldNewDto(String symbol, BigDecimal oldPrice, BigDecimal newPrice) {}