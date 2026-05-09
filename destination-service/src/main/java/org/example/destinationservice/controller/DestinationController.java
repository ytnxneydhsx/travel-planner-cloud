package org.example.destinationservice.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.common.web.ApiResponse;
import org.example.destinationservice.dto.DestinationCreateRequest;
import org.example.destinationservice.dto.DestinationBatchQueryRequest;
import org.example.destinationservice.dto.DestinationResponse;
import org.example.destinationservice.dto.DestinationUpdateRequest;
import org.example.destinationservice.service.DestinationService;
import org.springframework.util.StringUtils;
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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/destinations")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @PostMapping
    public ApiResponse<DestinationResponse> create(@Valid @RequestBody DestinationCreateRequest request) {
        DestinationResponse response = destinationService.create(request);
        return ApiResponse.success("Destination created successfully.", response);
    }

    @GetMapping("/{id}")
    public ApiResponse<DestinationResponse> getById(@PathVariable Long id) {
        DestinationResponse response = destinationService.getById(id);
        if (response == null) {
            throw new ResponseStatusException(NOT_FOUND, "Destination not found.");
        }
        return ApiResponse.success(response);
    }

    @PostMapping("/query")
    public ApiResponse<List<DestinationResponse>> listByIds(
            @Valid @RequestBody DestinationBatchQueryRequest request) {
        return ApiResponse.success(destinationService.listByIds(request.getDestinationIds()));
    }

    @GetMapping
    public ApiResponse<List<DestinationResponse>> search(
            @RequestParam(required = false) String namePrefix,
            @RequestParam(required = false) String keyword) {
        if (StringUtils.hasText(namePrefix)) {
            return ApiResponse.success(destinationService.searchByNamePrefix(namePrefix));
        }
        if (StringUtils.hasText(keyword)) {
            return ApiResponse.success(destinationService.searchByKeyword(keyword));
        }
        throw new ResponseStatusException(BAD_REQUEST, "Either namePrefix or keyword must be provided.");
    }

    @PutMapping("/{id}")
    public ApiResponse<DestinationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody DestinationUpdateRequest request) {
        DestinationResponse existing = destinationService.getById(id);
        if (existing == null) {
            throw new ResponseStatusException(NOT_FOUND, "Destination not found.");
        }

        DestinationResponse response = destinationService.update(id, request);
        return ApiResponse.success("Destination updated successfully.", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteById(@PathVariable Long id) {
        DestinationResponse existing = destinationService.getById(id);
        if (existing == null) {
            throw new ResponseStatusException(NOT_FOUND, "Destination not found.");
        }

        destinationService.deleteById(id);
        return ApiResponse.success("Destination deleted successfully.", null);
    }
}
