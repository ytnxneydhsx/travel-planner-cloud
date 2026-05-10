package org.example.common.web.trace.generator;

import java.util.UUID;

public class UuidTraceIdGenerator implements TraceIdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
