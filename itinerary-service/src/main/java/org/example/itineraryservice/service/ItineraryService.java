package org.example.itineraryservice.service;

import java.util.List;
import org.example.itineraryservice.dto.ItineraryCreateRequest;
import org.example.itineraryservice.dto.ItineraryDestinationAddRequest;
import org.example.itineraryservice.dto.ItineraryResponse;
import org.example.itineraryservice.dto.ItineraryUpdateRequest;

public interface ItineraryService {

    ItineraryResponse create(Long currentUserId, ItineraryCreateRequest request);

    ItineraryResponse getById(Long id, Long currentUserId);

    List<ItineraryResponse> listByUserId(Long currentUserId);

    ItineraryResponse update(Long id, Long currentUserId, ItineraryUpdateRequest request);

    void deleteById(Long id, Long currentUserId);

    ItineraryResponse addDestination(Long itineraryId, Long currentUserId, ItineraryDestinationAddRequest request);

    ItineraryResponse removeDestination(Long itineraryId, Long currentUserId, Long destinationId);
}
