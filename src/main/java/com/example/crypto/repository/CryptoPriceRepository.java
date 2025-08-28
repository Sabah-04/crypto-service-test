package com.example.crypto.repository;


import com.example.crypto.dto.CryptoMinMaxDto;
import com.example.crypto.dto.CryptoOldNewDto;
import com.example.crypto.dto.LatestTimeStampDto;
import com.example.crypto.model.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {


    @Query("SELECT c.symbol FROM CryptoPrice c")
    List<String> findAllSymbols();

    @Query("SELECT cp.symbol, MIN(cp.price), MAX(cp.price) FROM CryptoPrice cp group by cp.symbol")
    List<CryptoMinMaxDto> findMinMaxPriceForAllCrypto();

    @Query("SELECT cp.symbol, MIN(cp.price), MAX(cp.price) FROM CryptoPrice cp WHERE cp.timeStamp BETWEEN :start AND :end GROUP BY cp.symbol")
    List<CryptoMinMaxDto> findMinMaxPriceByDay(@Param("start") Long start, @Param("end") Long end);

    @Query("SELECT cp.symbol, MAX(cp.timeStamp) FROM CryptoPrice cp group by cp.symbol")
    List<LatestTimeStampDto> findLatestTimeStampGroupBySymbol();

    @Query("SELECT cp.symbol, MIN(cp.price), MAX(cp.price) FROM CryptoPrice cp WHERE cp.symbol = :symbol")
    Optional<CryptoMinMaxDto> findMinMaxPriceBySymbol(String symbol);

    @Query(value = """
    SELECT 
        :symbol AS symbol,
        (SELECT price FROM crypto_prices WHERE symbol = :symbol ORDER BY time_stamp ASC LIMIT 1) AS oldPrice,
        (SELECT price FROM crypto_prices WHERE symbol = :symbol ORDER BY time_stamp DESC LIMIT 1) AS newPrice
    """, nativeQuery = true)
    Optional<CryptoOldNewDto> findOldAndNewPriceBySymbol(@Param("symbol") String symbol);




}
