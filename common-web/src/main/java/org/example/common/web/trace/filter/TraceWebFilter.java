package org.example.common.web.trace.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.example.common.web.RequestHeaderNames;
import org.example.common.web.trace.context.TraceContext;
import org.example.common.web.trace.generator.TraceIdGenerator;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class TraceWebFilter extends OncePerRequestFilter {

    private final TraceIdGenerator traceIdGenerator;

    public TraceWebFilter(TraceIdGenerator traceIdGenerator) {
        this.traceIdGenerator = traceIdGenerator;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String traceId = resolveHeaderValue(request, RequestHeaderNames.TRACE_ID);
        String requestId = resolveHeaderValue(request, RequestHeaderNames.REQUEST_ID);

        TraceContext.bind(request, traceId, requestId);
        response.setHeader(RequestHeaderNames.TRACE_ID, traceId);
        response.setHeader(RequestHeaderNames.REQUEST_ID, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            TraceContext.clear();
        }
    }

    private String resolveHeaderValue(HttpServletRequest request, String headerName) {
        String headerValue = request.getHeader(headerName);
        if (StringUtils.hasText(headerValue)) {
            return headerValue;
        }

        return traceIdGenerator.generate();
    }
}
