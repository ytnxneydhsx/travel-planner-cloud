package org.example.itineraryservice.client;

import org.example.common.web.ApiResponse;
import org.example.itineraryservice.client.dto.DestinationBatchQueryRequest;
import org.example.itineraryservice.client.dto.DestinationSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "destination-service")
public interface DestinationClient {

    @GetMapping("/destinations/{id}")
    ApiResponse<DestinationSummary> getById(@PathVariable("id") Long id);

    @PostMapping("/destinations/query")
    ApiResponse<java.util.List<DestinationSummary>> getByIds(@RequestBody DestinationBatchQueryRequest request);
}
