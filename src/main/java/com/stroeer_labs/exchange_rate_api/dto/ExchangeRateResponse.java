package com.stroeer_labs.exchange_rate_api.dto;

import java.util.HashMap;
import java.util.Map;

public class ExchangeRateResponse {
    private String base;
    private String date;
    private Map<String, Double> rates = new HashMap<>();

    public ExchangeRateResponse() {
        // No-args constructor
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}
