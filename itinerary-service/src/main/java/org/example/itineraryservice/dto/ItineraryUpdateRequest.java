package org.example.itineraryservice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryUpdateRequest {

    @Size(max = 100, message = "title length must be less than or equal to 100.")
    private String title;

    @Size(max = 500, message = "description length must be less than or equal to 500.")
    private String description;
}
