package org.example.itineraryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDestinationAddRequest {

    @NotNull(message = "destinationId must not be null.")
    @Min(value = 1, message = "destinationId must be greater than 0.")
    private Long destinationId;
}
