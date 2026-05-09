package org.example.itineraryservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.example.common.web.ApiResponse;
import org.example.common.web.RequestHeaderNames;
import org.example.itineraryservice.dto.ItineraryCreateRequest;
import org.example.itineraryservice.dto.ItineraryDestinationAddRequest;
import org.example.itineraryservice.dto.ItineraryResponse;
import org.example.itineraryservice.dto.ItineraryUpdateRequest;
import org.example.itineraryservice.service.ItineraryService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/itineraries")
public class ItineraryController {

    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    @PostMapping
    public ApiResponse<ItineraryResponse> create(
            @RequestHeader(RequestHeaderNames.CURRENT_USER_ID) @Min(1) Long currentUserId,
            @Valid @RequestBody ItineraryCreateRequest request) {
        return ApiResponse.success("Itinerary created successfully.", itineraryService.create(currentUserId, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ItineraryResponse> getById(
            @RequestHeader(RequestHeaderNames.CURRENT_USER_ID) @Min(1) Long currentUserId,
            @PathVariable Long id) {
        return ApiResponse.success(itineraryService.getById(id, currentUserId));
    }

    @GetMapping
    public ApiResponse<List<ItineraryResponse>> listByUserId(
            @RequestHeader(RequestHeaderNames.CURRENT_USER_ID) @Min(1) Long currentUserId) {
        return ApiResponse.success(itineraryService.listByUserId(currentUserId));
    }

    @PutMapping("/{id}")
    public ApiResponse<ItineraryResponse> update(
            @RequestHeader(RequestHeaderNames.CURRENT_USER_ID) @Min(1) Long currentUserId,
            @PathVariable Long id,
            @Valid @RequestBody ItineraryUpdateRequest request) {
        return ApiResponse.success(
                "Itinerary updated successfully.",
                itineraryService.update(id, currentUserId, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteById(
            @RequestHeader(RequestHeaderNames.CURRENT_USER_ID) @Min(1) Long currentUserId,
            @PathVariable Long id) {
        itineraryService.deleteById(id, currentUserId);
        return ApiResponse.success("Itinerary deleted successfully.", null);
    }

    @PostMapping("/{id}/destinations")
    public ApiResponse<ItineraryResponse> addDestination(
            @RequestHeader(RequestHeaderNames.CURRENT_USER_ID) @Min(1) Long currentUserId,
            @PathVariable Long id,
            @Valid @RequestBody ItineraryDestinationAddRequest request) {
        return ApiResponse.success(
                "Destination added successfully.",
                itineraryService.addDestination(id, currentUserId, request));
    }

    @DeleteMapping("/{id}/destinations/{destinationId}")
    public ApiResponse<ItineraryResponse> removeDestination(
            @RequestHeader(RequestHeaderNames.CURRENT_USER_ID) @Min(1) Long currentUserId,
            @PathVariable Long id,
            @PathVariable Long destinationId) {
        return ApiResponse.success(
                "Destination removed successfully.",
                itineraryService.removeDestination(id, currentUserId, destinationId));
    }
}
