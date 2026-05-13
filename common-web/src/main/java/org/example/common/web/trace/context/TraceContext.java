package org.example.common.web.trace.context;

import jakarta.servlet.http.HttpServletRequest;
import org.example.common.web.trace.constant.TraceConstants;
import org.example.common.web.trace.constant.TraceMdcKeys;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

public final class TraceContext {

    private TraceContext() {
    }

    public static void bind(HttpServletRequest request, String traceId) {
        bind(request, traceId, null);
    }

    public static void bind(HttpServletRequest request, String traceId, String requestId) {
        request.setAttribute(TraceConstants.TRACE_ID_ATTRIBUTE, traceId);
        request.setAttribute(TraceConstants.REQUEST_ID_ATTRIBUTE, requestId);
        bindCurrent(traceId, requestId);
    }

    public static void bindCurrent(String traceId, String requestId) {
        putOrRemove(TraceMdcKeys.TRACE_ID, traceId);
        putOrRemove(TraceMdcKeys.REQUEST_ID, requestId);
    }

    public static void bindCurrentTraceId(String traceId) {
        putOrRemove(TraceMdcKeys.TRACE_ID, traceId);
    }

    public static void bindCurrentRequestId(String requestId) {
        putOrRemove(TraceMdcKeys.REQUEST_ID, requestId);
    }

    public static String getTraceId(HttpServletRequest request) {
        return getStringAttribute(request, TraceConstants.TRACE_ID_ATTRIBUTE);
    }

    public static String getRequestId(HttpServletRequest request) {
        return getStringAttribute(request, TraceConstants.REQUEST_ID_ATTRIBUTE);
    }

    public static String getCurrentTraceId() {
        return MDC.get(TraceMdcKeys.TRACE_ID);
    }

    public static String getCurrentRequestId() {
        return MDC.get(TraceMdcKeys.REQUEST_ID);
    }

    public static void clear() {
        MDC.remove(TraceMdcKeys.TRACE_ID);
        MDC.remove(TraceMdcKeys.REQUEST_ID);
    }

    private static String getStringAttribute(HttpServletRequest request, String attributeName) {
        Object attributeValue = request.getAttribute(attributeName);
        if (attributeValue instanceof String stringValue) {
            return stringValue;
        }

        return null;
    }

    private static void putOrRemove(String key, String value) {
        if (StringUtils.hasText(value)) {
            MDC.put(key, value);
            return;
        }

        MDC.remove(key);
    }
}
