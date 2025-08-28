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
    public String getMinPrice() {
        return minPrice != null ? minPrice.stripTrailingZeros().toPlainString() : null;
    }

    public String getMaxPrice() {
        return maxPrice != null ? maxPrice.stripTrailingZeros().toPlainString() : null;
    }

    public String getOldestPrice() {
        return oldestPrice != null ? oldestPrice.stripTrailingZeros().toPlainString() : null;
    }

    public String getNewestPrice() {
        return newestPrice != null ? newestPrice.stripTrailingZeros().toPlainString() : null;
    }

}
