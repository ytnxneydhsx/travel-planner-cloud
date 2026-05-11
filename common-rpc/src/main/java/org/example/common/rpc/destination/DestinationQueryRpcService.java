package org.example.common.rpc.destination;

import java.util.List;
import org.example.common.rpc.destination.dto.DestinationSummaryRpcDTO;

public interface DestinationQueryRpcService {

    List<DestinationSummaryRpcDTO> listByIds(List<Long> destinationIds);
}
