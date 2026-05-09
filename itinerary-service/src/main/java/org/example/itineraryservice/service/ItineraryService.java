package org.example.itineraryservice.service;

import java.util.List;
import org.example.itineraryservice.dto.ItineraryCreateRequest;
import org.example.itineraryservice.dto.ItineraryDestinationAddRequest;
import org.example.itineraryservice.dto.ItineraryResponse;
import org.example.itineraryservice.dto.ItineraryUpdateRequest;

public interface ItineraryService {

    ItineraryResponse create(ItineraryCreateRequest request);

    ItineraryResponse getById(Long id);

    List<ItineraryResponse> listByUserId(Long userId);

    ItineraryResponse update(Long id, ItineraryUpdateRequest request);

    void deleteById(Long id);

    ItineraryResponse addDestination(Long itineraryId, ItineraryDestinationAddRequest request);

    ItineraryResponse removeDestination(Long itineraryId, Long destinationId);
}
