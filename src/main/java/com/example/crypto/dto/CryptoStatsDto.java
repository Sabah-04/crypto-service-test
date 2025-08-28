package com.example.crypto.dto;


import lombok.*;

import java.math.BigDecimal;


@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class CryptoStatsDto {

    private String symbol;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal oldestPrice;
    private BigDecimal newestPrice;


    // Override Lombok getters for clean JSON output
    public BigDecimal getMinPrice() {
        return minPrice.stripTrailingZeros();
    }

    public BigDecimal getMaxPrice() {
        return maxPrice.stripTrailingZeros();
    }

    public BigDecimal getOldestPrice() {
        return oldestPrice.stripTrailingZeros();
    }

    public BigDecimal getNewestPrice() {
        return newestPrice.stripTrailingZeros();
    }

}
