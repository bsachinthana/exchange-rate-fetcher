package com.stroeer_labs.exchange_rate_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class ExchangeRateApiApplication {

	@Autowired
	private CacheManager cacheManager;

	public static void main(String[] args) {
		SpringApplication.run(ExchangeRateApiApplication.class, args);
	}

	// Clear all caches every day at midnight
	@Scheduled(cron = "0 0 0 * * *")
	public void clearAllCaches() {
		cacheManager.getCacheNames().forEach(name -> {
			var cache = cacheManager.getCache(name);
			if (cache != null) {
				cache.clear();
			}
		});
	}
}
