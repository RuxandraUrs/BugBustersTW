package com.smartrestaurant.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator restaurantRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/restaurant/api/categories/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "menu-service")
                                .addResponseHeader("X-Service", "menu-service")
                                .rewritePath("/restaurant/(?<segment>.*)", "/${segment}")                        )
                        .uri("lb://menu-service"))
                .route(p -> p
                        .path("/restaurant/api/dishes/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "menu-service")
                                .addResponseHeader("X-Service", "menu-service")
                                .rewritePath("/restaurant/(?<segment>.*)", "/${segment}")                        )
                        .uri("lb://menu-service"))
                .route(p -> p
                        .path("/restaurant/api/ingredients/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "menu-service")
                                .addResponseHeader("X-Service", "menu-service")
                                .rewritePath("/restaurant/(?<segment>.*)", "/${segment}")                        )
                        .uri("lb://menu-service"))
                .route(p -> p
                        .path("/restaurant/api/orders/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "order-service")
                                .addResponseHeader("X-Service", "order-service")
                                .rewritePath("/restaurant/api/orders(?<segment>.*)", "/api/orders${segment}")
                        )
                        .uri("lb://order-service"))
                .route(p -> p
                        .path("/restaurant/api/users/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "user-service")
                                .addResponseHeader("X-Service", "user-service")
                                .rewritePath("/restaurant/(?<segment>.*)", "/${segment}")
                        )
                        .uri("lb://user-service"))

                .route(p -> p
                        .path("/restaurant/menu-service/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "menu-service")
                                .addResponseHeader("X-Service", "menu-service")
                                .rewritePath("/restaurant/menu-service/(?<segment>.*)", "/api/menu-service/${segment}")
                        )
                        .uri("lb://menu-service"))

                .build();
    }
}
