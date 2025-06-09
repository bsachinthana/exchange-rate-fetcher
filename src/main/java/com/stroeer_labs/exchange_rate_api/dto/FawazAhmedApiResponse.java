package com.stroeer_labs.exchange_rate_api.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class FawazAhmedApiResponse {
    private String date;
    private Map<String, Map<String, Double>> rates = new HashMap<>();

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Map<String, Double>> getRates() {
        return rates;
    }

    public void setRates(Map<String, Map<String, Double>> currencies) {
        this.rates = currencies;
    }

    
    @JsonAnySetter
    public void addCurrency(String key, Map<String, Double> value) {
        this.rates.put(key, value);
    }


}
