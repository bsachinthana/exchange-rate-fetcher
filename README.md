# Description
A Spring Boot REST API that fetches and averages exchange rates for multiple currencies from multiple external APIs, featuring in-memory caching and a metrics endpoint.

# Approach

## Choice of Technology
- **Spring Boot & Java:** Chosen for familiarity and suitability for building REST APIs.
- **Caffeine:** Used as the in-memory cache for its simplicity, performance, and popularity with Spring Boot.

## Caching Strategy
- The cache key format is `{BaseCur}:{symbol1,symbol2}`.
- All currency codes are converted to uppercase to ensure consistency and case-insensitive behaviour.
- Symbols are sorted so that different orders (e.g., `EUR:NZD,USD` and `EUR:USD,NZD`) map to the same cache entry, ensuring cache hits regardless of symbol order.

## Metrics
A custom metrics class is used, as the requirement was simple counters for API hits.

## Other Features
- Use of Data Transfer Object for better maintainability.
- Exception handling for external queries, returning relevant status codes.

## Testing
The `/exchangeRate` API was tested for the following conditions:
- Valid query
- Invalid query
- Ensured cache hits and correct metric counters via `/metrics` values


# Future Work

## Caching Strategy Improvements

There are potential enhancements to the caching strategy that could improve efficiency and reduce external API calls:

1. **Cache All Rates for a Base Currency on First Request**
   
   When a request is made for a specific base currency, fetch and cache all available exchange rates for that base currency from the external APIs (since both APIs have endpoints which return all rates for a given base).
   - For subsequent requests with the same base currency, retrieve the cached rates and filter them for the requested symbols.
   - **Pros:** Significantly reduces the number of external API calls for the same base currency.
   - **Cons:** Increases memory usage, as the cache may store many unused currency pairs.

2. **Partial Cache Hits and Symbol Merging**
   - Allow the cache to return partial results if some, but not all, requested symbols are already cached for a base currency.
   - For missing symbols, fetch only those from the external APIs, then merge them into the existing cache entry.
   - This could be implemented by storing symbols as a set per base currency and updating the cache as new symbols are requested.
   - **Pros:** Reduces redundant API calls and avoids storing unnecessary data.
   - **Cons:** Adds complexity to cache management and rate tracking.
