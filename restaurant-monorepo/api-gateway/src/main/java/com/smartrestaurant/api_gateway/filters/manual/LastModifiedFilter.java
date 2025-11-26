package com.smartrestaurant.api_gateway.filters.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LastModifiedFilter implements GlobalFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod httpMethod = exchange.getRequest().getMethod();

        if(httpMethod == HttpMethod.GET && path.contains("/users")) {
            logger.info("Users last modified");

            return chain.filter(exchange).then(
                    Mono.fromRunnable(() -> {
                        HttpHeaders headers = exchange.getResponse().getHeaders();

                        String lastModified = ZonedDateTime.now(ZoneId.of("GMT"))
                                .format(DateTimeFormatter.RFC_1123_DATE_TIME);

                        headers.set(HttpHeaders.LAST_MODIFIED, lastModified);

                        logger.info("Last modified header: {}", lastModified);
                    })
            );
        }

        return chain.filter(exchange);
    }
}
