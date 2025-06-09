package com.stroeer_labs.exchange_rate_api;

import com.stroeer_labs.exchange_rate_api.dto.ExchangeRateResponse;
import com.stroeer_labs.exchange_rate_api.dto.MetricsResponse;
import com.stroeer_labs.exchange_rate_api.service.MetricsService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExchangeRateApiApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    void contextLoads() {
    }


    @Test
    void validQuery_returnsExchangeRate() {
        ResponseEntity<ExchangeRateResponse> response = restTemplate.getForEntity("/exchangeRates/EUR?symbols=NZD", ExchangeRateResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ExchangeRateResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getBase().toLowerCase()).isEqualTo("eur");
        assertThat(body.getDate()).isInstanceOf(String.class);
        assertThat(body.getRates()).containsKey("NZD");
        assertThat(body.getRates().get("NZD")).isInstanceOf(Number.class);
    }

	@Test
    void cacheHit_metricsReflectsSingleApiRequest() {

        // cache miss - as previous test validQuery_returnsExchangeRate adds a cache entry
        restTemplate.getForEntity("/exchangeRates/EUR?symbols=NZD", ExchangeRateResponse.class);
    
		// At this point 
        ResponseEntity<MetricsResponse> metricsResp = restTemplate.getForEntity("/metrics", MetricsResponse.class);
        assertThat(metricsResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        MetricsResponse body = metricsResp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTotalQueries()).isEqualTo(2);

        var apis = body.getApis();
        assertThat(apis).isNotEmpty();
        MetricsResponse.ApiMetrics firstApi = apis.get(1);
        assertThat(firstApi.getMetrics().get("totalRequests")).isEqualTo(1);
    }

    @Test
    void invalidQuery_returns502With404Message() {
        ResponseEntity<String> response = restTemplate.getForEntity("/exchangeRates/INVALID?symbols=NZD", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).startsWith("404");
    }


}
