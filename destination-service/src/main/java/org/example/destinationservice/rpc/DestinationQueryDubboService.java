package org.example.destinationservice.rpc;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.dubbo.config.annotation.DubboService;
import org.example.common.rpc.destination.DestinationQueryRpcService;
import org.example.common.rpc.destination.dto.DestinationSummaryRpcDTO;
import org.example.destinationservice.dto.DestinationResponse;
import org.example.destinationservice.service.DestinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DubboService
public class DestinationQueryDubboService implements DestinationQueryRpcService {

    private static final Logger log = LoggerFactory.getLogger(DestinationQueryDubboService.class);

    private final DestinationService destinationService;

    public DestinationQueryDubboService(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @Override
    public List<DestinationSummaryRpcDTO> listByIds(List<Long> destinationIds) {
        long startTime = System.nanoTime();
        log.info("Destination RPC provider listByIds received: destinationCount={}, destinationIds={}",
                sizeOf(destinationIds), destinationIds);

        try {
            List<DestinationSummaryRpcDTO> result = destinationService.listByIds(destinationIds).stream()
                    .map(this::toRpcDto)
                    .toList();
            log.info("Destination RPC provider listByIds completed: destinationCount={}, resultCount={}, durationMs={}",
                    sizeOf(destinationIds), sizeOf(result), calculateDurationMillis(startTime));
            return result;
        } catch (RuntimeException exception) {
            log.error("Destination RPC provider listByIds failed: destinationCount={}, destinationIds={}, durationMs={}",
                    sizeOf(destinationIds), destinationIds, calculateDurationMillis(startTime), exception);
            throw exception;
        }
    }

    private DestinationSummaryRpcDTO toRpcDto(DestinationResponse destination) {
        DestinationSummaryRpcDTO rpcDto = new DestinationSummaryRpcDTO();
        rpcDto.setId(destination.getId());
        rpcDto.setName(destination.getName());
        rpcDto.setRegionCode(destination.getRegionCode());
        rpcDto.setRegionName(destination.getRegionName());
        rpcDto.setSummary(destination.getSummary());
        rpcDto.setCoverImageUrl(destination.getCoverImageUrl());
        return rpcDto;
    }

    private int sizeOf(List<?> values) {
        return values == null ? 0 : values.size();
    }

    private long calculateDurationMillis(long startTime) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }
}
