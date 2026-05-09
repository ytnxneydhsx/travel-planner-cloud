package org.example.destinationservice.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinationUpdateRequest {

    @Size(max = 100, message = "name length must be less than or equal to 100.")
    private String name;

    @Pattern(regexp = "^\\d{6}$", message = "regionCode must be a 6-digit code.")
    private String regionCode;

    @Size(max = 255, message = "address length must be less than or equal to 255.")
    private String address;

    @Size(max = 255, message = "summary length must be less than or equal to 255.")
    private String summary;

    private String description;

    @Size(max = 500, message = "coverImageUrl length must be less than or equal to 500.")
    private String coverImageUrl;

    @Min(value = 0, message = "status must be 0 or 1.")
    @Max(value = 1, message = "status must be 0 or 1.")
    private Integer status;

    @AssertTrue(message = "At least one updatable field must be provided.")
    public boolean hasUpdatableField() {
        return name != null
                || regionCode != null
                || address != null
                || summary != null
                || description != null
                || coverImageUrl != null
                || status != null;
    }
}
