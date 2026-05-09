package org.example.itineraryservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinationSummary {

    private Long id;

    private String name;

    private String regionCode;

    private String regionName;

    private String summary;

    private String coverImageUrl;
}
