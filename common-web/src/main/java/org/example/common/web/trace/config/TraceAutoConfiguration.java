package org.example.common.web.trace.config;

import org.example.common.web.trace.filter.TraceWebFilter;
import org.example.common.web.trace.generator.TraceIdGenerator;
import org.example.common.web.trace.generator.UuidTraceIdGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class TraceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TraceIdGenerator traceIdGenerator() {
        return new UuidTraceIdGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public TraceWebFilter traceWebFilter(TraceIdGenerator traceIdGenerator) {
        return new TraceWebFilter(traceIdGenerator);
    }

    @Bean
    public FilterRegistrationBean<TraceWebFilter> traceWebFilterRegistration(TraceWebFilter traceWebFilter) {
        FilterRegistrationBean<TraceWebFilter> registration = new FilterRegistrationBean<>(traceWebFilter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return registration;
    }
}
