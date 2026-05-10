package org.example.common.web.trace.context;

import jakarta.servlet.http.HttpServletRequest;
import org.example.common.web.trace.constant.TraceConstants;
import org.example.common.web.trace.constant.TraceMdcKeys;
import org.slf4j.MDC;

public final class TraceContext {

    private TraceContext() {
    }

    public static void bind(HttpServletRequest request, String traceId) {
        request.setAttribute(TraceConstants.TRACE_ID_ATTRIBUTE, traceId);
        MDC.put(TraceMdcKeys.TRACE_ID, traceId);
    }

    public static String getTraceId(HttpServletRequest request) {
        Object traceId = request.getAttribute(TraceConstants.TRACE_ID_ATTRIBUTE);
        if (traceId instanceof String traceIdValue) {
            return traceIdValue;
        }

        return null;
    }

    public static String getCurrentTraceId() {
        return MDC.get(TraceMdcKeys.TRACE_ID);
    }

    public static void clear() {
        MDC.remove(TraceMdcKeys.TRACE_ID);
    }
}
