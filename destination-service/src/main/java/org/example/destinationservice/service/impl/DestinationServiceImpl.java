package org.example.destinationservice.service.impl;

import java.util.List;
import org.example.destinationservice.dto.DestinationCreateRequest;
import org.example.destinationservice.dto.DestinationResponse;
import org.example.destinationservice.dto.DestinationUpdateRequest;
import org.example.destinationservice.entity.Destination;
import org.example.destinationservice.mapper.DestinationMapper;
import org.example.destinationservice.service.DestinationService;
import org.example.destinationservice.support.region.RegionDictionary;
import org.springframework.stereotype.Service;

@Service
public class DestinationServiceImpl implements DestinationService {

    private final DestinationMapper destinationMapper;

    private final RegionDictionary regionDictionary;

    public DestinationServiceImpl(
            DestinationMapper destinationMapper,
            RegionDictionary regionDictionary) {
        this.destinationMapper = destinationMapper;
        this.regionDictionary = regionDictionary;
    }

    @Override
    public DestinationResponse create(DestinationCreateRequest request) {
        Destination destination = Destination.builder()
                .name(request.getName())
                .regionCode(request.getRegionCode())
                .address(request.getAddress())
                .summary(request.getSummary())
                .description(request.getDescription())
                .coverImageUrl(request.getCoverImageUrl())
                .status(request.getStatus())
                .build();

        destinationMapper.insert(destination);
        return toResponse(destinationMapper.selectById(destination.getId()));
    }

    @Override
    public DestinationResponse getById(Long id) {
        return toResponse(destinationMapper.selectById(id));
    }

    @Override
    public List<DestinationResponse> searchByNamePrefix(String namePrefix) {
        return destinationMapper.selectByNamePrefix(namePrefix).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<DestinationResponse> searchByKeyword(String keyword) {
        return destinationMapper.selectByNameKeyword(keyword).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public DestinationResponse update(Long id, DestinationUpdateRequest request) {
        Destination destination = Destination.builder()
                .id(id)
                .name(request.getName())
                .regionCode(request.getRegionCode())
                .address(request.getAddress())
                .summary(request.getSummary())
                .description(request.getDescription())
                .coverImageUrl(request.getCoverImageUrl())
                .status(request.getStatus())
                .build();

        destinationMapper.updateById(destination);
        return toResponse(destinationMapper.selectById(id));
    }

    @Override
    public void deleteById(Long id) {
        destinationMapper.deleteById(id);
    }

    private DestinationResponse toResponse(Destination destination) {
        if (destination == null) {
            return null;
        }

        return DestinationResponse.builder()
                .id(destination.getId())
                .name(destination.getName())
                .regionCode(destination.getRegionCode())
                .regionName(regionDictionary.findNameByCode(destination.getRegionCode()).orElse(null))
                .address(destination.getAddress())
                .summary(destination.getSummary())
                .description(destination.getDescription())
                .coverImageUrl(destination.getCoverImageUrl())
                .status(destination.getStatus())
                .createdAt(destination.getGmtCreate())
                .updatedAt(destination.getGmtModified())
                .build();
    }
}
