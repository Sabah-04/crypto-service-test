package com.example.crypto.dto;

import java.math.BigDecimal;

public record CryptoNormDto(String symbol, BigDecimal normalizedRange) {}