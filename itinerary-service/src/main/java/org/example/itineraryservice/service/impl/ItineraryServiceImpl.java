package org.example.itineraryservice.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.example.common.web.BusinessException;
import org.example.itineraryservice.client.dto.DestinationSummary;
import org.example.itineraryservice.dto.ItineraryCreateRequest;
import org.example.itineraryservice.dto.ItineraryDestinationAddRequest;
import org.example.itineraryservice.dto.ItineraryDestinationResponse;
import org.example.itineraryservice.dto.ItineraryResponse;
import org.example.itineraryservice.dto.ItineraryUpdateRequest;
import org.example.itineraryservice.entity.Itinerary;
import org.example.itineraryservice.entity.ItineraryDestination;
import org.example.itineraryservice.manager.DestinationManager;
import org.example.itineraryservice.mapper.ItineraryDestinationMapper;
import org.example.itineraryservice.mapper.ItineraryMapper;
import org.example.itineraryservice.service.ItineraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ItineraryServiceImpl implements ItineraryService {

    private static final Logger log = LoggerFactory.getLogger(ItineraryServiceImpl.class);

    private final ItineraryMapper itineraryMapper;

    private final ItineraryDestinationMapper itineraryDestinationMapper;

    private final DestinationManager destinationManager;

    public ItineraryServiceImpl(
            ItineraryMapper itineraryMapper,
            ItineraryDestinationMapper itineraryDestinationMapper,
            DestinationManager destinationManager) {
        this.itineraryMapper = itineraryMapper;
        this.itineraryDestinationMapper = itineraryDestinationMapper;
        this.destinationManager = destinationManager;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItineraryResponse create(Long currentUserId, ItineraryCreateRequest request) {
        long startTime = System.nanoTime();
        List<Long> destinationIds = request.getDestinationIds();
        log.info("Create itinerary started: userId={}, title={}, destinationCount={}",
                currentUserId, request.getTitle(), sizeOf(destinationIds));

        validateDestinationIds(request.getDestinationIds());
        validateDestinationsExist(request.getDestinationIds());

        Itinerary itinerary = Itinerary.builder()
                .userId(currentUserId)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        itineraryMapper.insert(itinerary);

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

        ItineraryResponse response = getById(itinerary.getId(), currentUserId);
        log.info("Create itinerary completed: userId={}, itineraryId={}, destinationCount={}, durationMs={}",
                currentUserId, itinerary.getId(), sizeOf(destinationIds), calculateDurationMillis(startTime));
        return response;
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
    @Transactional(rollbackFor = Exception.class)
    public ItineraryResponse addDestination(
            Long itineraryId,
            Long currentUserId,
            ItineraryDestinationAddRequest request) {
        long startTime = System.nanoTime();
        log.info("Add destination to itinerary started: userId={}, itineraryId={}, destinationId={}",
                currentUserId, itineraryId, request.getDestinationId());

        requireOwnedItinerary(itineraryId, currentUserId);
        ItineraryDestination existing = itineraryDestinationMapper.selectByItineraryIdAndDestinationId(
                itineraryId,
                request.getDestinationId());
        if (existing != null) {
            log.warn("Duplicate destination add rejected before insert: itineraryId={}, destinationId={}",
                    itineraryId, request.getDestinationId());
            throw new BusinessException(CONFLICT, "Destination already exists in itinerary.");
        }

        destinationManager.getRequiredById(request.getDestinationId());
        Integer maxSortOrder = itineraryDestinationMapper.selectMaxSortOrderByItineraryId(itineraryId);

        try {
            itineraryDestinationMapper.insert(ItineraryDestination.builder()
                    .itineraryId(itineraryId)
                    .destinationId(request.getDestinationId())
                    .sortOrder((maxSortOrder == null ? 0 : maxSortOrder) + 1)
                    .build());
        } catch (DuplicateKeyException exception) {
            log.warn("Duplicate destination add rejected by unique index: itineraryId={}, destinationId={}",
                    itineraryId, request.getDestinationId(), exception);
            throw new BusinessException(CONFLICT, "Destination already exists in itinerary.");
        }

        ItineraryResponse response = getById(itineraryId, currentUserId);
        log.info("Add destination to itinerary completed: userId={}, itineraryId={}, destinationId={}, durationMs={}",
                currentUserId, itineraryId, request.getDestinationId(), calculateDurationMillis(startTime));
        return response;
    }

    @Override
    public ItineraryResponse removeDestination(Long itineraryId, Long currentUserId, Long destinationId) {
        requireOwnedItinerary(itineraryId, currentUserId);
        ItineraryDestination existing = itineraryDestinationMapper.selectByItineraryIdAndDestinationId(
                itineraryId,
                destinationId);
        if (existing == null) {
            log.warn("Remove destination rejected because association does not exist: itineraryId={}, destinationId={}",
                    itineraryId, destinationId);
            throw new BusinessException(NOT_FOUND, "Destination does not exist in itinerary.");
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
            log.warn("Itinerary destination ids must be unique: {}", destinationIds);
            throw new BusinessException(BAD_REQUEST, "Destination ids must be unique.");
        }
    }

    private void validateDestinationsExist(List<Long> destinationIds) {
        destinationManager.validateRequiredIds(destinationIds);
    }

    private ItineraryResponse toResponse(Itinerary itinerary) {
        if (itinerary == null) {
            return null;
        }

        List<ItineraryDestination> itineraryDestinations = itineraryDestinationMapper.selectByItineraryId(itinerary.getId());
        if (itineraryDestinations == null) {
            itineraryDestinations = Collections.emptyList();
        }

        List<Long> destinationIds = itineraryDestinations.stream()
                .map(ItineraryDestination::getDestinationId)
                .distinct()
                .toList();
        java.util.Map<Long, DestinationSummary> destinationMap = destinationManager.getByIds(destinationIds);

        List<ItineraryDestinationResponse> destinationResponses = itineraryDestinations.stream()
                .map(itineraryDestination -> toDestinationResponse(itineraryDestination, destinationMap))
                .toList();

        return ItineraryResponse.builder()
                .id(itinerary.getId())
                .userId(itinerary.getUserId())
                .title(itinerary.getTitle())
                .description(itinerary.getDescription())
                .destinations(destinationResponses)
                .createdAt(itinerary.getGmtCreate())
                .updatedAt(itinerary.getGmtModified())
                .build();
    }

    private ItineraryDestinationResponse toDestinationResponse(
            ItineraryDestination itineraryDestination,
            java.util.Map<Long, DestinationSummary> destinationMap) {
        DestinationSummary destination = destinationMap.get(itineraryDestination.getDestinationId());

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
            log.warn("Itinerary not found when checking ownership: itineraryId={}, currentUserId={}",
                    itineraryId, currentUserId);
            throw new BusinessException(NOT_FOUND, "Itinerary not found.");
        }
        if (!itinerary.getUserId().equals(currentUserId)) {
            log.warn("Forbidden itinerary access rejected: itineraryId={}, ownerUserId={}, currentUserId={}",
                    itineraryId, itinerary.getUserId(), currentUserId);
            throw new BusinessException(FORBIDDEN, "Forbidden.");
        }
        return itinerary;
    }

    private int sizeOf(List<?> values) {
        return values == null ? 0 : values.size();
    }

    private long calculateDurationMillis(long startTime) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }
}
