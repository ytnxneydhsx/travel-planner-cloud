package org.example.itineraryservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.example.common.web.ApiResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Validated
@RestController
@RequestMapping("/itineraries")
public class ItineraryController {

    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    @PostMapping
    public ApiResponse<ItineraryResponse> create(@Valid @RequestBody ItineraryCreateRequest request) {
        return ApiResponse.success("Itinerary created successfully.", itineraryService.create(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ItineraryResponse> getById(@PathVariable Long id) {
        ItineraryResponse response = itineraryService.getById(id);
        if (response == null) {
            throw new ResponseStatusException(NOT_FOUND, "Itinerary not found.");
        }
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<ItineraryResponse>> listByUserId(@RequestParam @Min(1) Long userId) {
        return ApiResponse.success(itineraryService.listByUserId(userId));
    }

    @PutMapping("/{id}")
    public ApiResponse<ItineraryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ItineraryUpdateRequest request) {
        if (itineraryService.getById(id) == null) {
            throw new ResponseStatusException(NOT_FOUND, "Itinerary not found.");
        }
        return ApiResponse.success("Itinerary updated successfully.", itineraryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteById(@PathVariable Long id) {
        if (itineraryService.getById(id) == null) {
            throw new ResponseStatusException(NOT_FOUND, "Itinerary not found.");
        }
        itineraryService.deleteById(id);
        return ApiResponse.success("Itinerary deleted successfully.", null);
    }

    @PostMapping("/{id}/destinations")
    public ApiResponse<ItineraryResponse> addDestination(
            @PathVariable Long id,
            @Valid @RequestBody ItineraryDestinationAddRequest request) {
        if (itineraryService.getById(id) == null) {
            throw new ResponseStatusException(NOT_FOUND, "Itinerary not found.");
        }
        return ApiResponse.success("Destination added successfully.", itineraryService.addDestination(id, request));
    }

    @DeleteMapping("/{id}/destinations/{destinationId}")
    public ApiResponse<ItineraryResponse> removeDestination(
            @PathVariable Long id,
            @PathVariable Long destinationId) {
        if (itineraryService.getById(id) == null) {
            throw new ResponseStatusException(NOT_FOUND, "Itinerary not found.");
        }
        return ApiResponse.success(
                "Destination removed successfully.",
                itineraryService.removeDestination(id, destinationId));
    }
}
