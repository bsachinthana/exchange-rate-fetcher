package com.stroeer_labs.exchange_rate_api.dto;

import java.util.List;
import java.util.Map;

public class MetricsResponse {
    private long totalQueries;
    private List<ApiMetrics> apis;

    public MetricsResponse(long totalQueries, List<ApiMetrics> apis) {
        this.totalQueries = totalQueries;
        this.apis = apis;
    }

    public long getTotalQueries() {
        return totalQueries;
    }

    public void setTotalQueries(long totalQueries) {
        this.totalQueries = totalQueries;
    }

    public List<ApiMetrics> getApis() {
        return apis;
    }

    public void setApis(List<ApiMetrics> apis) {
        this.apis = apis;
    }

    public static class ApiMetrics {
        private String name;
        private Map<String, Long> metrics;

        public ApiMetrics(String name, Map<String, Long> metrics) {
            this.name = name;
            this.metrics = metrics;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, Long> getMetrics() {
            return metrics;
        }

        public void setMetrics(Map<String, Long> metrics) {
            this.metrics = metrics;
        }
    }
}
