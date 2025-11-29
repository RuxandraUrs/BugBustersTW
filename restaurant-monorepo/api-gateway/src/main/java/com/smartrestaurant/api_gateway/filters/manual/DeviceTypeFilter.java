package com.smartrestaurant.api_gateway.filters.manual;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;

public class DeviceTypeFilter {
    @Bean
    public GlobalFilter preGlobalFilterDeviceType() {
        return (exchange, chain) -> {
            String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
            String deviceType = "Desktop";

            if (userAgent != null && (userAgent.contains("Mobile") || userAgent.contains("Android") || userAgent.contains("iPhone"))) {
                deviceType = "Mobile";
            }

            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-Device-Type", deviceType)
                            .build())
                    .build();

            modifiedExchange.getResponse().getHeaders().add("X-Device-Detected", deviceType);
            return chain.filter(modifiedExchange);
        };
    }
}
