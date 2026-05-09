package org.example.itineraryservice.client;

import org.example.common.web.ApiResponse;
import org.example.itineraryservice.client.dto.DestinationSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "destination-service", url = "${destination-service.base-url}")
public interface DestinationClient {

    @GetMapping("/destinations/{id}")
    ApiResponse<DestinationSummary> getById(@PathVariable("id") Long id);
}
