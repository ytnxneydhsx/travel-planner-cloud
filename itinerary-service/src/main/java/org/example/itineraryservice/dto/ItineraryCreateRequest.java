package org.example.itineraryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryCreateRequest {

    @NotBlank(message = "title must not be blank.")
    @Size(max = 100, message = "title length must be less than or equal to 100.")
    private String title;

    @Size(max = 500, message = "description length must be less than or equal to 500.")
    private String description;

    private List<@NotNull(message = "destinationId must not be null.")
            @Min(value = 1, message = "destinationId must be greater than 0.") Long> destinationIds;
}
