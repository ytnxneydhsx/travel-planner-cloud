package org.example.itineraryservice.service.impl;

import feign.FeignException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.example.common.web.ApiResponse;
import org.example.itineraryservice.client.DestinationClient;
import org.example.itineraryservice.client.dto.DestinationSummary;
import org.example.itineraryservice.dto.ItineraryCreateRequest;
import org.example.itineraryservice.dto.ItineraryDestinationAddRequest;
import org.example.itineraryservice.dto.ItineraryDestinationResponse;
import org.example.itineraryservice.dto.ItineraryResponse;
import org.example.itineraryservice.dto.ItineraryUpdateRequest;
import org.example.itineraryservice.entity.Itinerary;
import org.example.itineraryservice.entity.ItineraryDestination;
import org.example.itineraryservice.mapper.ItineraryDestinationMapper;
import org.example.itineraryservice.mapper.ItineraryMapper;
import org.example.itineraryservice.service.ItineraryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ItineraryServiceImpl implements ItineraryService {

    private final ItineraryMapper itineraryMapper;

    private final ItineraryDestinationMapper itineraryDestinationMapper;

    private final DestinationClient destinationClient;

    public ItineraryServiceImpl(
            ItineraryMapper itineraryMapper,
            ItineraryDestinationMapper itineraryDestinationMapper,
            DestinationClient destinationClient) {
        this.itineraryMapper = itineraryMapper;
        this.itineraryDestinationMapper = itineraryDestinationMapper;
        this.destinationClient = destinationClient;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItineraryResponse create(Long currentUserId, ItineraryCreateRequest request) {
        validateDestinationIds(request.getDestinationIds());
        validateDestinationsExist(request.getDestinationIds());

        Itinerary itinerary = Itinerary.builder()
                .userId(currentUserId)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        itineraryMapper.insert(itinerary);

        List<Long> destinationIds = request.getDestinationIds();
        if (destinationIds != null) {
            for (int i = 0; i < destinationIds.size(); i++) {
                Long destinationId = destinationIds.get(i);
                itineraryDestinationMapper.insert(ItineraryDestination.builder()
                        .itineraryId(itinerary.getId())
                        .destinationId(destinationId)
                        .sortOrder(i + 1)
                        .build());
            }
        }

        return getById(itinerary.getId(), currentUserId);
    }

    @Override
    public ItineraryResponse getById(Long id, Long currentUserId) {
        return toResponse(requireOwnedItinerary(id, currentUserId));
    }

    @Override
    public List<ItineraryResponse> listByUserId(Long currentUserId) {
        return itineraryMapper.selectByUserId(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ItineraryResponse update(Long id, Long currentUserId, ItineraryUpdateRequest request) {
        requireOwnedItinerary(id, currentUserId);
        itineraryMapper.updateById(Itinerary.builder()
                .id(id)
                .title(request.getTitle())
                .description(request.getDescription())
                .build());
        return getById(id, currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id, Long currentUserId) {
        requireOwnedItinerary(id, currentUserId);
        itineraryDestinationMapper.deleteByItineraryId(id);
        itineraryMapper.deleteById(id);
    }

    @Override
    public ItineraryResponse addDestination(
            Long itineraryId,
            Long currentUserId,
            ItineraryDestinationAddRequest request) {
        requireOwnedItinerary(itineraryId, currentUserId);
        ItineraryDestination existing = itineraryDestinationMapper.selectByItineraryIdAndDestinationId(
                itineraryId,
                request.getDestinationId());
        if (existing != null) {
            throw new ResponseStatusException(CONFLICT, "Destination already exists in itinerary.");
        }

        requireDestination(request.getDestinationId());
        Integer maxSortOrder = itineraryDestinationMapper.selectMaxSortOrderByItineraryId(itineraryId);

        itineraryDestinationMapper.insert(ItineraryDestination.builder()
                .itineraryId(itineraryId)
                .destinationId(request.getDestinationId())
                .sortOrder((maxSortOrder == null ? 0 : maxSortOrder) + 1)
                .build());

        return getById(itineraryId, currentUserId);
    }

    @Override
    public ItineraryResponse removeDestination(Long itineraryId, Long currentUserId, Long destinationId) {
        requireOwnedItinerary(itineraryId, currentUserId);
        ItineraryDestination existing = itineraryDestinationMapper.selectByItineraryIdAndDestinationId(
                itineraryId,
                destinationId);
        if (existing == null) {
            throw new ResponseStatusException(NOT_FOUND, "Destination does not exist in itinerary.");
        }

        itineraryDestinationMapper.deleteByItineraryIdAndDestinationId(itineraryId, destinationId);
        return getById(itineraryId, currentUserId);
    }

    private void validateDestinationIds(List<Long> destinationIds) {
        if (destinationIds == null || destinationIds.isEmpty()) {
            return;
        }

        Set<Long> uniqueDestinationIds = new HashSet<>(destinationIds);
        if (uniqueDestinationIds.size() != destinationIds.size()) {
            throw new ResponseStatusException(BAD_REQUEST, "Destination ids must be unique.");
        }
    }

    private void validateDestinationsExist(List<Long> destinationIds) {
        if (destinationIds == null || destinationIds.isEmpty()) {
            return;
        }

        for (Long destinationId : destinationIds) {
            requireDestination(destinationId);
        }
    }

    private DestinationSummary requireDestination(Long destinationId) {
        DestinationSummary destination = findDestination(destinationId);
        if (destination == null) {
            throw new ResponseStatusException(NOT_FOUND, "Destination not found.");
        }
        return destination;
    }

    private DestinationSummary findDestination(Long destinationId) {
        try {
            ApiResponse<DestinationSummary> response = destinationClient.getById(destinationId);
            if (response == null || !response.isSuccess()) {
                return null;
            }
            return response.getData();
        } catch (FeignException.NotFound exception) {
            return null;
        } catch (FeignException exception) {
            throw new ResponseStatusException(BAD_GATEWAY, "Destination service request failed.");
        }
    }

    private ItineraryResponse toResponse(Itinerary itinerary) {
        if (itinerary == null) {
            return null;
        }

        List<ItineraryDestination> itineraryDestinations = itineraryDestinationMapper.selectByItineraryId(itinerary.getId());
        if (itineraryDestinations == null) {
            itineraryDestinations = Collections.emptyList();
        }

        List<ItineraryDestinationResponse> destinations = itineraryDestinations.stream()
                .map(this::toDestinationResponse)
                .toList();

        return ItineraryResponse.builder()
                .id(itinerary.getId())
                .userId(itinerary.getUserId())
                .title(itinerary.getTitle())
                .description(itinerary.getDescription())
                .destinations(destinations)
                .createdAt(itinerary.getCreatedAt())
                .updatedAt(itinerary.getUpdatedAt())
                .build();
    }

    private ItineraryDestinationResponse toDestinationResponse(ItineraryDestination itineraryDestination) {
        DestinationSummary destination = findDestination(itineraryDestination.getDestinationId());

        return ItineraryDestinationResponse.builder()
                .destinationId(itineraryDestination.getDestinationId())
                .name(destination == null ? null : destination.getName())
                .regionCode(destination == null ? null : destination.getRegionCode())
                .regionName(destination == null ? null : destination.getRegionName())
                .summary(destination == null ? null : destination.getSummary())
                .coverImageUrl(destination == null ? null : destination.getCoverImageUrl())
                .sortOrder(itineraryDestination.getSortOrder())
                .build();
    }

    private Itinerary requireOwnedItinerary(Long itineraryId, Long currentUserId) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new ResponseStatusException(NOT_FOUND, "Itinerary not found.");
        }
        if (!itinerary.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(FORBIDDEN, "Forbidden.");
        }
        return itinerary;
    }
}
