package org.example.destinationservice.rpc;

import java.util.List;
import org.apache.dubbo.config.annotation.DubboService;
import org.example.common.rpc.destination.DestinationQueryRpcService;
import org.example.common.rpc.destination.dto.DestinationSummaryRpcDTO;
import org.example.destinationservice.dto.DestinationResponse;
import org.example.destinationservice.service.DestinationService;

@DubboService
public class DestinationQueryDubboService implements DestinationQueryRpcService {

    private final DestinationService destinationService;

    public DestinationQueryDubboService(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @Override
    public List<DestinationSummaryRpcDTO> listByIds(List<Long> destinationIds) {
        return destinationService.listByIds(destinationIds).stream()
                .map(this::toRpcDto)
                .toList();
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
}
