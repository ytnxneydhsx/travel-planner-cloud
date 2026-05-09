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
public class Itinerary {

    private Long id;

    private Long userId;

    private String title;

    private String description;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
