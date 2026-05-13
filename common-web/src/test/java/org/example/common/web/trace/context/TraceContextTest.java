package org.example.common.web.trace.context;

import org.example.common.web.RequestHeaderNames;
import org.example.common.web.trace.constant.TraceMdcKeys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class TraceContextTest {

    @AfterEach
    void tearDown() {
        TraceContext.clear();
    }

    @Test
    void bindStoresTraceIdAndRequestIdInRequestAttributesAndMdc() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        TraceContext.bind(request, "trace-123", "request-456");

        assertThat(TraceContext.getTraceId(request)).isEqualTo("trace-123");
        assertThat(TraceContext.getRequestId(request)).isEqualTo("request-456");
        assertThat(MDC.get(TraceMdcKeys.TRACE_ID)).isEqualTo("trace-123");
        assertThat(MDC.get(TraceMdcKeys.REQUEST_ID)).isEqualTo("request-456");
        assertThat(RequestHeaderNames.REQUEST_ID).isEqualTo("X-Request-Id");
    }

    @Test
    void clearRemovesTraceIdAndRequestIdFromMdc() {
        TraceContext.bindCurrent("trace-123", "request-456");

        TraceContext.clear();

        assertThat(MDC.get(TraceMdcKeys.TRACE_ID)).isNull();
        assertThat(MDC.get(TraceMdcKeys.REQUEST_ID)).isNull();
    }
}
