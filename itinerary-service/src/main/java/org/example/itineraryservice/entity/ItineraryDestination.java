package org.example.itineraryservice.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDestination {

    private Long id;

    private Long itineraryId;

    private Long destinationId;

    private Integer sortOrder;

    private LocalDateTime gmtCreate;
}
