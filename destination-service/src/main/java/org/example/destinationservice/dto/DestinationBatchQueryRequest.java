package org.example.destinationservice.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinationBatchQueryRequest {

    @NotEmpty(message = "destinationIds must not be empty.")
    private List<@NotNull(message = "destinationId must not be null.")
            @Min(value = 1, message = "destinationId must be greater than 0.") Long> destinationIds;

    @AssertTrue(message = "destinationIds must be unique.")
    public boolean hasUniqueDestinationIds() {
        if (destinationIds == null || destinationIds.isEmpty()) {
            return true;
        }
        Set<Long> uniqueDestinationIds = new HashSet<>(destinationIds);
        return uniqueDestinationIds.size() == destinationIds.size();
    }
}
