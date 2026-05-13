package org.example.itineraryservice.rpc;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.dubbo.config.annotation.DubboReference;
import org.example.common.rpc.destination.DestinationQueryRpcService;
import org.example.common.rpc.destination.dto.DestinationSummaryRpcDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DestinationDubboClient {

    private static final Logger log = LoggerFactory.getLogger(DestinationDubboClient.class);

    @DubboReference(check = false)
    private DestinationQueryRpcService destinationQueryRpcService;

    public List<DestinationSummaryRpcDTO> listByIds(List<Long> destinationIds) {
        long startTime = System.nanoTime();
        log.info("Destination RPC listByIds started: destinationCount={}, destinationIds={}",
                sizeOf(destinationIds), destinationIds);

        try {
            List<DestinationSummaryRpcDTO> result = destinationQueryRpcService.listByIds(destinationIds);
            log.info("Destination RPC listByIds completed: destinationCount={}, resultCount={}, durationMs={}",
                    sizeOf(destinationIds), sizeOf(result), calculateDurationMillis(startTime));
            return result;
        } catch (RuntimeException exception) {
            log.error("Destination RPC listByIds failed: destinationCount={}, destinationIds={}, durationMs={}",
                    sizeOf(destinationIds), destinationIds, calculateDurationMillis(startTime), exception);
            throw exception;
        }
    }

    private int sizeOf(List<?> values) {
        return values == null ? 0 : values.size();
    }

    private long calculateDurationMillis(long startTime) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }
}
