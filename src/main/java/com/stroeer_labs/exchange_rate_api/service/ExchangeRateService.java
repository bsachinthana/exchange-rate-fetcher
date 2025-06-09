package com.stroeer_labs.exchange_rate_api.service;

import com.stroeer_labs.exchange_rate_api.dto.ExchangeRateResponse;
import com.stroeer_labs.exchange_rate_api.dto.FawazAhmedApiResponse;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class ExchangeRateService {

    private static final Logger logger = Logger.getLogger(ExchangeRateService.class.getName());

    private final RestClient restClient;
    private final MetricsService metricsService;

    @Autowired
    public ExchangeRateService(RestClient.Builder restClientBuilder, MetricsService metricsService) {
        this.restClient = restClientBuilder.build();
        this.metricsService = metricsService;
    }

    @Cacheable(
        value = "exchangeRates",
        key = "T(com.stroeer_labs.exchange_rate_api.service.ExchangeRateService).buildCacheKey(#baseCur, #symbols)"
    )
    public ExchangeRateResponse getExchangeRates(String baseCur, String symbols) {
        metricsService.incExchangeRatesRequest();
        logger.info("Cache miss for base: " + baseCur + ", symbols: " + symbols + ". Fetching from APIs.");

        // Prepare requested symbols as uppercase set, sorted for consistency
        Set<String> requestedSymbols = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        ExchangeRateResponse response1 = fetchFromFrankfurter(baseCur, String.join(",", requestedSymbols));
        ExchangeRateResponse response2 = fetchFromFawazAhmetApi(baseCur, requestedSymbols);

        Map<String, Double> rates1 = response1.getRates();
        Map<String, Double> rates2 = response2.getRates();

        // Only average the requested symbols (case-insensitive)
        Map<String, Double> averagedRates = new HashMap<>();
        for (String symbol : requestedSymbols) {
            // Use case-insensitive lookup
            Double v1 = getRateIgnoreCase(rates1, symbol);
            Double v2 = getRateIgnoreCase(rates2, symbol);

            if (v1 != null && v2 != null) {
                averagedRates.put(symbol, (v1 + v2) / 2.0);
            } else if (v1 != null) {
                averagedRates.put(symbol, v1);
            } else if (v2 != null) {
                averagedRates.put(symbol, v2);
            }
            // else: skip if neither API has the symbol
        }

        ExchangeRateResponse averagedResponse = new ExchangeRateResponse();
        averagedResponse.setBase(baseCur);
        averagedResponse.setRates(averagedRates);
        averagedResponse.setDate(response1.getDate());

        metricsService.incExchangeRatesResponse();
        return averagedResponse;
    }

    public static String buildCacheKey(String baseCur, String symbols) {
        String normalizedBase = baseCur == null ? "" : baseCur.trim().toUpperCase();
        String normalizedSymbols = symbols == null ? "" :
                Arrays.stream(symbols.split(","))
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .sorted()
                        .collect(Collectors.joining(","));
        logger.info("Building cache key for base: " + normalizedBase + ", symbols: " + normalizedSymbols);
        return normalizedBase + ":" + normalizedSymbols;
    }

    private Double getRateIgnoreCase(Map<String, Double> rates, String symbol) {
        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(symbol)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private ExchangeRateResponse fetchFromFrankfurter(String baseCur, String symbols) {
        metricsService.incFrankfurterApiRequest();
        String url = UriComponentsBuilder.fromUriString("https://api.frankfurter.dev/v1/latest")
                .queryParam("base", baseCur)
                .queryParam("symbols", symbols)
                .toUriString();
        try {
            ExchangeRateResponse resp = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(ExchangeRateResponse.class);
            metricsService.incFrankfurterApiResponse();
            return resp;
        } catch (WebClientResponseException e) {
            logger.severe("Frankfurter API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        }
    }

    private ExchangeRateResponse fetchFromFawazAhmetApi(String baseCur, Set<String> requestedSymbols) {
        metricsService.incFawazAhmedApiRequest();
        // FawazAhmed API only support lower case base currency and symbols
        var baseCurLowerCase = baseCur.toLowerCase();
        var requestedSymbolsLC = requestedSymbols.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        String url ="https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/" + baseCurLowerCase + ".json";
        try {
            FawazAhmedApiResponse rawResponse = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(FawazAhmedApiResponse.class);

            ExchangeRateResponse response = new ExchangeRateResponse();
            response.setBase(baseCur);

            Map<String, Double> rates = new HashMap<>();
            Map<String, Double> ratesObj = rawResponse.getRates().get(baseCurLowerCase);

            for (String symbol : requestedSymbolsLC) {
                // Key from Fawaz Ahmed API is always lowercase
                if (ratesObj.containsKey(symbol)) {
                    rates.put(symbol.toUpperCase(), ratesObj.get(symbol));
                }
                else {
                    // If not found, log a warning
                    logger.warning("Symbol " + symbol + " not found in Fawaz Ahmed API");
                }
            }
            response.setRates(rates);
            metricsService.incFawazAhmedApiResponse();
            return response;
        } catch (WebClientResponseException e) {
            logger.severe("Fawaz Ahmed API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        }
    }
}