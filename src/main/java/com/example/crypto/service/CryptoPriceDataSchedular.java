package com.example.crypto.service;

import com.example.crypto.config.utils.CsvLoaderUtil;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class CryptoPriceDataSchedular {

    @Value("${crypto.csv.update.file.path}")
    private String csvFilePath;

    private final CryptoPriceService service;

    // Runs every day at 2 AM
    @Scheduled(cron = "0 0 2 * * *")
    @SchedulerLock(name = "dailyCsvLoad", lockAtMostFor = "1h", lockAtLeastFor = "5m")
    public void loadDailyIncrement() {
        System.out.println("Running daily incremental load...");

        // Load files from `classpath:data/daily/YYYY-MM-DD_values.csv`
        CsvLoaderUtil.loadIncrementalFiles(csvFilePath, service);
    }
}