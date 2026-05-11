package org.example.itineraryservice.rpc;

import java.util.List;
import org.apache.dubbo.config.annotation.DubboReference;
import org.example.common.rpc.destination.DestinationQueryRpcService;
import org.example.common.rpc.destination.dto.DestinationSummaryRpcDTO;
import org.springframework.stereotype.Component;

@Component
public class DestinationDubboClient {

    @DubboReference(check = false)
    private DestinationQueryRpcService destinationQueryRpcService;

    public List<DestinationSummaryRpcDTO> listByIds(List<Long> destinationIds) {
        return destinationQueryRpcService.listByIds(destinationIds);
    }
}
