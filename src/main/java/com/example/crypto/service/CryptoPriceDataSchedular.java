package com.example.crypto.service;

import com.example.crypto.config.utils.CsvLoaderUtil;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class CryptoPriceDataSchedular {

    private final CacheManager cacheManager;
    private final CryptoPriceService service;

    @Value("${crypto.csv.update.file.path}")
    private String csvFilePath;

    // Runs every day at 2 AM
    @Scheduled(cron = "0 0 2 * * *")
    @SchedulerLock(name = "dailyCsvLoad", lockAtMostFor = "1h", lockAtLeastFor = "5m")
    public void loadDailyIncrement() {
        System.out.println("Running daily incremental load...");


        CsvLoaderUtil.loadIncrementalFiles(csvFilePath, service);

        Objects.requireNonNull(cacheManager.getCache("cryptoStats")).clear();
        System.out.println("Evicted cache for cryptoStats after daily load");

    }
}