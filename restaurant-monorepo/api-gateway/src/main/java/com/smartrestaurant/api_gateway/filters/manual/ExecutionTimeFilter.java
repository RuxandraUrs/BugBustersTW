package com.smartrestaurant.api_gateway.filters.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class ExecutionTimeFilter {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionTimeFilter.class);

    @Bean
    public GlobalFilter postGlobalFilterExecutionTime() {
        return (exchange, chain) -> {
            long startTime = System.currentTimeMillis();

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;

                String requestPath = exchange.getRequest().getURI().getPath();

                logger.info("The request on [{}] took: {} ms", requestPath, duration);

                exchange.getResponse().getHeaders().add("X-Response-Time", duration + "ms");
            }));
        };
    }
}