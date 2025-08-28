package com.example.crypto.config.utils;

import com.example.crypto.model.CryptoPrice;
import com.example.crypto.service.CryptoPriceService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class CsvLoaderUtil {

    private static final int BATCH_SIZE = 1000;

    private static void loadCsv(
            String fileLocationPattern,
            CryptoPriceService cryptoService,
            Predicate<CSVRecord> recordFilter,
            Function<CSVRecord, CryptoPrice> recordMapper
    ) {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(fileLocationPattern);

            Arrays.stream(resources).parallel().forEach(resource -> {
                try (InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
                     CSVParser csvParser = new CSVParser(inputStreamReader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

                    List<CryptoPrice> batch = new ArrayList<>();
                    for (CSVRecord csvRecord : csvParser) {
                        if (!isValidRowToInsert(csvRecord) || !recordFilter.test(csvRecord)) {
                            continue;
                        }

                        CryptoPrice price = recordMapper.apply(csvRecord);
                        batch.add(price);

                        if (batch.size() >= BATCH_SIZE) {
                            cryptoService.saveBatch(batch);
                            batch.clear();
                        }
                    }
                    if (!batch.isEmpty()) {
                        cryptoService.saveBatch(batch);
                    }

                } catch (Exception e) {
                    System.err.println("Error loading file " + resource.getFilename() + ": " + e.getMessage());
                    throw new RuntimeException("CSV initialization failed, partial data may be loaded", e);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("Error loading CSV files", e);
        }
    }


    public static void loadCsvFiles(String fileLocationPattern, CryptoPriceService cryptoService) {
        loadCsv(
                fileLocationPattern,
                cryptoService,
                csvRecord -> true, // accept all
                CsvLoaderUtil::mapToCryptoPrice
        );
    }

    public static void loadIncrementalFiles(String fileLocationPattern, CryptoPriceService cryptoService) {
        Map<String, Long> latestTimestamps = cryptoService.getLatestTimestamps();

        loadCsv(
                fileLocationPattern,
                cryptoService,
                csvRecord -> {
                    long csvTimestamp = Long.parseLong(csvRecord.get("timestamp"));
                    return !latestTimestamps.containsKey(csvRecord.get("symbol"))
                            || csvTimestamp > latestTimestamps.get(csvRecord.get("symbol"));
                },
                CsvLoaderUtil::mapToCryptoPrice
        );
    }

    private static CryptoPrice mapToCryptoPrice(CSVRecord csvRecord) {
        CryptoPrice price = new CryptoPrice();
        price.setSymbol(csvRecord.get("symbol"));
        price.setTimeStamp(Long.parseLong(csvRecord.get("timestamp")));
        price.setPrice(new BigDecimal(csvRecord.get("price")));
        return price;
    }

    public static boolean isValidRowToInsert(CSVRecord record) {
        if (record.get("timestamp") == null || record.get("symbol") == null || record.get("price") == null) {
            return false;
        }

        try {
            Long.parseLong(record.get("timestamp"));
            new BigDecimal(record.get("price"));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
