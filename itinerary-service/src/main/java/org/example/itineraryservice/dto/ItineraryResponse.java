package org.example.itineraryservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryResponse {

    private Long id;

    private Long userId;

    private String title;

    private String description;

    private List<ItineraryDestinationResponse> destinations;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
