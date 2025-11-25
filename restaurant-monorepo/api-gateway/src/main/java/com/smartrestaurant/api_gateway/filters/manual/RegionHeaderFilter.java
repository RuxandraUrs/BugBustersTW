package com.smartrestaurant.api_gateway.filters.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Configuration
public class RegionHeaderFilter {

    private static final Logger logger = LoggerFactory.getLogger(RegionHeaderFilter.class);

    @Bean
    @Order(2)
    public GlobalFilter postGlobalFilterRegion() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable( () ->{
                String region = "EU-RO-Brasov-Node1";

                exchange.getResponse().getHeaders().add("X-Server-Region", region);

                logger.info("Response served from the region: {}", region);
            }));
        };
    }
}
