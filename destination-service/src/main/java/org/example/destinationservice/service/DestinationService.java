package org.example.destinationservice.service;

import java.util.List;
import org.example.destinationservice.dto.DestinationCreateRequest;
import org.example.destinationservice.dto.DestinationResponse;
import org.example.destinationservice.dto.DestinationUpdateRequest;

public interface DestinationService {

    DestinationResponse create(DestinationCreateRequest request);

    DestinationResponse getById(Long id);

    List<DestinationResponse> listByIds(List<Long> ids);

    List<DestinationResponse> searchByNamePrefix(String namePrefix);

    List<DestinationResponse> searchByKeyword(String keyword);

    DestinationResponse update(Long id, DestinationUpdateRequest request);

    void deleteById(Long id);
}
