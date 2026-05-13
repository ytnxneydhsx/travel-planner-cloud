package org.example.gatewayservice.logging;

public final class TraceContextConstants {

    public static final String TRACE_ID_ATTRIBUTE = "traceId";

    public static final String REQUEST_ID_ATTRIBUTE = "requestId";

    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    public static final String TRACE_ID_MDC_KEY = "traceId";

    public static final String REQUEST_ID_MDC_KEY = "requestId";

    private TraceContextConstants() {
    }
}
