package com.example.crypto.config;


import com.example.crypto.config.utils.CsvLoaderUtil;
import com.example.crypto.service.CryptoPriceService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Profile("init")
public class CryptoDataInitializer {

    private final CryptoPriceService cryptoPriceService;

    @Value("${crypto.csv.file.path}")
    private String csvFilePath;

    @PostConstruct
    public void loadInitialData() {
        if(cryptoPriceService.hasData()) {
            return;
        }
        CsvLoaderUtil.loadCsvFiles(csvFilePath, cryptoPriceService);
    }
}
