package com.stroeer_labs.exchange_rate_api.controller;

import com.stroeer_labs.exchange_rate_api.dto.ExchangeRateResponse;
import com.stroeer_labs.exchange_rate_api.service.ExchangeRateService;
import com.stroeer_labs.exchange_rate_api.service.MetricsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exchangeRates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private final MetricsService metricsService;

    public ExchangeRateController(ExchangeRateService exchangeRateService, MetricsService metricsService) {
        this.exchangeRateService = exchangeRateService;
        this.metricsService = metricsService;
    }

    @GetMapping("/{baseCur}")
    public ExchangeRateResponse getExchangeRates(
            @PathVariable String baseCur,
            @RequestParam String symbols) {
        metricsService.incExchangeRatesEndpointHit();
        return exchangeRateService.getExchangeRates(baseCur, symbols);
    }
}
