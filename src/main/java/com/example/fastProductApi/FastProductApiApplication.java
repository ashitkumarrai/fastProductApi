package com.example.fastProductApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;

import java.util.concurrent.TimeUnit;

import static com.example.fastProductApi.service.ProductServiceForBulkCrud.executorService;


@SpringBootApplication
@EnableCaching
@EnableRetry
public class FastProductApiApplication {
    static Logger log = LoggerFactory.getLogger(FastProductApiApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(FastProductApiApplication.class, args);
        Runtime.getRuntime().addShutdownHook(new Thread(FastProductApiApplication::shutdownExecutor));
    }

    // Shutdown ExecutorService gracefully
    public static void shutdownExecutor() {
        log.info("Commencing graceful ExecutorService shutdown. Waiting for active requests to complete");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

}
