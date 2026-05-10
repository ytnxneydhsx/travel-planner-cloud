package org.example.common.web.trace.config;

import feign.RequestInterceptor;
import org.example.common.web.trace.interceptor.TraceFeignRequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(RequestInterceptor.class)
public class TraceFeignAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor traceFeignRequestInterceptor() {
        return new TraceFeignRequestInterceptor();
    }
}
