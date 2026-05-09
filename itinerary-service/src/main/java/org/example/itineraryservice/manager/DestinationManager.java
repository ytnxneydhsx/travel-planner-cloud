package org.example.itineraryservice.manager;

import feign.FeignException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.common.web.ApiResponse;
import org.example.common.web.BusinessException;
import org.example.itineraryservice.client.DestinationClient;
import org.example.itineraryservice.client.dto.DestinationBatchQueryRequest;
import org.example.itineraryservice.client.dto.DestinationSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
public class DestinationManager {

    private static final Logger log = LoggerFactory.getLogger(DestinationManager.class);

    private final DestinationClient destinationClient;

    public DestinationManager(DestinationClient destinationClient) {
        this.destinationClient = destinationClient;
    }

    public DestinationSummary getRequiredById(Long destinationId) {
        DestinationSummary destination = getByIdOrNull(destinationId);
        if (destination == null) {
            log.warn("Destination not found when validating itinerary relation: destinationId={}", destinationId);
            throw new BusinessException(NOT_FOUND, "Destination not found.");
        }
        return destination;
    }

    public DestinationSummary getByIdOrNull(Long destinationId) {
        Map<Long, DestinationSummary> destinations = getByIds(List.of(destinationId));
        return destinations.get(destinationId);
    }

    public Map<Long, DestinationSummary> getByIds(Collection<Long> destinationIds) {
        if (destinationIds == null || destinationIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            ApiResponse<List<DestinationSummary>> response = destinationClient.getByIds(
                    DestinationBatchQueryRequest.builder()
                            .destinationIds(List.copyOf(destinationIds))
                            .build());
            if (response == null || !response.isSuccess() || response.getData() == null) {
                return Collections.emptyMap();
            }
            return response.getData().stream()
                    .collect(Collectors.toMap(
                            DestinationSummary::getId,
                            destination -> destination,
                            (left, right) -> left,
                            LinkedHashMap::new));
        } catch (FeignException.NotFound exception) {
            return Collections.emptyMap();
        } catch (FeignException exception) {
            log.error("Destination service batch request failed: destinationIds={}", destinationIds, exception);
            throw new BusinessException(BAD_GATEWAY, "Destination service request failed.");
        }
    }

    public void validateRequiredIds(Collection<Long> destinationIds) {
        if (destinationIds == null || destinationIds.isEmpty()) {
            return;
        }

        Map<Long, DestinationSummary> destinationMap = getByIds(destinationIds);
        Set<Long> missingDestinationIds = destinationIds.stream()
                .filter(destinationId -> !destinationMap.containsKey(destinationId))
                .collect(Collectors.toSet());
        if (!missingDestinationIds.isEmpty()) {
            Long missingDestinationId = missingDestinationIds.iterator().next();
            log.warn("Destination not found when validating itinerary relation: destinationId={}", missingDestinationId);
            throw new BusinessException(NOT_FOUND, "Destination not found.");
        }
    }
}
