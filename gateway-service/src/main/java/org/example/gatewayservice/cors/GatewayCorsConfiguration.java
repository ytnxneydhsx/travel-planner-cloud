package org.example.gatewayservice.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayCorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter(GatewayCorsProperties gatewayCorsProperties) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(gatewayCorsProperties.getAllowedOriginPatterns());
        corsConfiguration.setAllowedMethods(gatewayCorsProperties.getAllowedMethods());
        corsConfiguration.setAllowedHeaders(gatewayCorsProperties.getAllowedHeaders());
        corsConfiguration.setAllowCredentials(gatewayCorsProperties.getAllowCredentials());
        corsConfiguration.setMaxAge(gatewayCorsProperties.getMaxAgeSeconds());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }
}
