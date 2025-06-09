package com.stroeer_labs.exchange_rate_api.controller;

import com.stroeer_labs.exchange_rate_api.service.MetricsService;
import com.stroeer_labs.exchange_rate_api.dto.MetricsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/metrics")
    public MetricsResponse getMetrics() {
        long totalQueries = metricsService.getExchangeRatesEndpointHit();

        MetricsResponse.ApiMetrics frankfurter = new MetricsResponse.ApiMetrics(
            "frankfurter",
            Map.of(
                "totalRequests", metricsService.getFrankfurterApiRequest(),
                "totalResponses", metricsService.getFrankfurterApiResponse()
            )
        );
        MetricsResponse.ApiMetrics fawazAhmed = new MetricsResponse.ApiMetrics(
            "fawazAhmed",
            Map.of(
                "totalRequests", metricsService.getFawazAhmedApiRequest(),
                "totalResponses", metricsService.getFawazAhmedApiResponse()
            )
        );

        return new MetricsResponse(
            totalQueries,
            List.of(frankfurter, fawazAhmed)
        );
    }
}
