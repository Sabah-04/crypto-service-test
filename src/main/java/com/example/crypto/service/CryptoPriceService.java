package com.example.crypto.service;

import com.example.crypto.dto.*;
import com.example.crypto.exception.CryptoNotFoundException;
import com.example.crypto.exception.UnsupportedCryptoException;
import com.example.crypto.model.CryptoPrice;
import com.example.crypto.repository.CryptoPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CryptoPriceService {

    private final CryptoPriceRepository cryptoPriceRepository;

    public CryptoStatsDto getCryptoStats(String symbol){

        if (!isSupportedCrypto(symbol)) {
            throw new UnsupportedCryptoException(symbol);
        }
        CryptoMinMaxDto minMax = cryptoPriceRepository.findMinMaxPriceBySymbol(symbol)
                .orElseThrow(() -> new CryptoNotFoundException("No stats found for the symbol: " + symbol));
        CryptoOldNewDto oldNewPrices = cryptoPriceRepository.findOldAndNewPriceBySymbol(symbol)
                .orElseThrow(() -> new CryptoNotFoundException("No stats found for the symbol: " + symbol));

        return CryptoStatsDto.builder().symbol(symbol)
                .minPrice(minMax.minPrice())
                .maxPrice(minMax.maxPrice())
                .oldestPrice(oldNewPrices.oldPrice())
                .newestPrice(oldNewPrices.newPrice()).build();
    }

    public List<CryptoNormDto> getCryptoSortedByNormRange(){

        List<CryptoMinMaxDto> minMaxPriceList =  cryptoPriceRepository.findMinMaxPriceForAllCrypto();
        if (minMaxPriceList == null || minMaxPriceList.isEmpty()) {
            throw new CryptoNotFoundException("No crypto data found.");
        }
        return minMaxPriceList.stream().filter(c -> c.minPrice().compareTo(BigDecimal.ZERO) > 0)
                .map(c -> new CryptoNormDto(
                        c.symbol(),
                        c.maxPrice().subtract(c.minPrice()).divide(c.minPrice(), 8, RoundingMode.HALF_UP).stripTrailingZeros()
                )).sorted((a, b) -> b.normalizedRange().compareTo(a.normalizedRange()))
                .toList();
    }

    public CryptoNormDto getHighestCryptoOfDay(LocalDate day){

        Long start = day.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        Long end = day.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        List<CryptoMinMaxDto> minMaxPriceList = cryptoPriceRepository.findMinMaxPriceByDay(start, end);
        if(minMaxPriceList == null || minMaxPriceList.isEmpty()){
            throw new CryptoNotFoundException("No crypto data found for the day: " + day);
        }
        Optional<CryptoNormDto> maxDayCrypto = minMaxPriceList.stream().filter(c -> c.minPrice().compareTo(BigDecimal.ZERO) > 0)
                .map(c -> {
                    BigDecimal range = c.maxPrice().subtract(c.minPrice()).divide(c.minPrice(), 8, RoundingMode.HALF_UP);
                    return new CryptoNormDto(c.symbol(), range.stripTrailingZeros());
                }).max(Comparator.comparing(CryptoNormDto::normalizedRange));

        return maxDayCrypto.orElseThrow(() -> new CryptoNotFoundException("No crypto data found for day: " + day));

    }

    public Map<String, Long> getLatestTimestamps() {
        List<LatestTimeStampDto> projections = cryptoPriceRepository.findLatestTimeStampGroupBySymbol();
        if (projections.isEmpty()) {
            return new HashMap<>();
        }
        return projections.stream()
                .collect(Collectors.toMap(
                        LatestTimeStampDto::symbol,
                        LatestTimeStampDto::timeStamp
                ));
    }


    public boolean isSupportedCrypto(String symbol) {

        List<String> allSymbols = cryptoPriceRepository.findAllSymbols();

        return allSymbols.stream()
                .anyMatch(s -> s.equalsIgnoreCase(symbol));

    }

    public void saveBatch(List<CryptoPrice> batch) {
         cryptoPriceRepository.saveAllAndFlush(batch);
    }

    public boolean hasData() {
        return cryptoPriceRepository.count() > 0;
    }

}
