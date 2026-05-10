package org.example.common.web.trace.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.example.common.web.RequestHeaderNames;
import org.example.common.web.trace.context.TraceContext;
import org.springframework.util.StringUtils;

public class TraceFeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String traceId = TraceContext.getCurrentTraceId();
        if (!StringUtils.hasText(traceId)) {
            return;
        }

        template.header(RequestHeaderNames.TRACE_ID, traceId);
    }
}
