package com.stroeer_labs.exchange_rate_api.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {
    private final AtomicLong frankfurterApiRequest = new AtomicLong();
    private final AtomicLong frankfurterApiResponse = new AtomicLong();
    private final AtomicLong fawazAhmedApiRequest = new AtomicLong();
    private final AtomicLong fawazAhmedApiResponse = new AtomicLong();
    private final AtomicLong exchangeRatesRequest = new AtomicLong();
    private final AtomicLong exchangeRatesResponse = new AtomicLong();
    private final AtomicLong exchangeRatesEndpointHit = new AtomicLong();

    public void incFrankfurterApiRequest() { frankfurterApiRequest.incrementAndGet(); }
    public void incFrankfurterApiResponse() { frankfurterApiResponse.incrementAndGet(); }
    public void incFawazAhmedApiRequest() { fawazAhmedApiRequest.incrementAndGet(); }
    public void incFawazAhmedApiResponse() { fawazAhmedApiResponse.incrementAndGet(); }
    public void incExchangeRatesRequest() { exchangeRatesRequest.incrementAndGet(); }
    public void incExchangeRatesResponse() { exchangeRatesResponse.incrementAndGet(); }
    public void incExchangeRatesEndpointHit() { exchangeRatesEndpointHit.incrementAndGet(); }

    public long getFrankfurterApiRequest() { return frankfurterApiRequest.get(); }
    public long getFrankfurterApiResponse() { return frankfurterApiResponse.get(); }
    public long getFawazAhmedApiRequest() { return fawazAhmedApiRequest.get(); }
    public long getFawazAhmedApiResponse() { return fawazAhmedApiResponse.get(); }
    public long getExchangeRatesRequest() { return exchangeRatesRequest.get(); }
    public long getExchangeRatesResponse() { return exchangeRatesResponse.get(); }
    public long getExchangeRatesEndpointHit() { return exchangeRatesEndpointHit.get(); }
}
