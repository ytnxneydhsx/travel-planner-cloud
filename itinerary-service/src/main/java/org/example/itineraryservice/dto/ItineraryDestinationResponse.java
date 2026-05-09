package org.example.itineraryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDestinationResponse {

    private Long destinationId;

    private String name;

    private String regionCode;

    private String regionName;

    private String summary;

    private String coverImageUrl;

    private Integer sortOrder;
}
