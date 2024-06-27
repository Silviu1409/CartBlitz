package com.example.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class RouteConfiguration {
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
				.route(p -> p
						.path("/cartblitz/coupon/**")
						.filters(f -> f.rewritePath("/cartblitz/coupon/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time",new Date().toString()))
						.uri("lb://coupon"))
                .route(p -> p
                        .path("/cartblitz/CartBlitz/**")
                        .filters(f -> f.rewritePath("/cartblitz/CartBlitz/(?<segment>.*)","/${segment}")
                                .addResponseHeader("X-Response-Time",new Date().toString()))
                        .uri("lb://CartBlitz")).build();
    }
}
