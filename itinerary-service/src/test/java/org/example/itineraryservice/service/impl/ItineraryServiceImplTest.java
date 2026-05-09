package org.example.itineraryservice.service.impl;

import java.util.List;
import org.example.common.web.ApiResponse;
import org.example.itineraryservice.client.DestinationClient;
import org.example.itineraryservice.client.dto.DestinationSummary;
import org.example.itineraryservice.dto.ItineraryCreateRequest;
import org.example.itineraryservice.dto.ItineraryDestinationAddRequest;
import org.example.itineraryservice.entity.Itinerary;
import org.example.itineraryservice.entity.ItineraryDestination;
import org.example.itineraryservice.mapper.ItineraryDestinationMapper;
import org.example.itineraryservice.mapper.ItineraryMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ItineraryServiceImplTest {

    @Test
    void createRejectsDuplicateDestinationIds() {
        ItineraryMapper itineraryMapper = mock(ItineraryMapper.class);
        ItineraryDestinationMapper itineraryDestinationMapper = mock(ItineraryDestinationMapper.class);
        DestinationClient destinationClient = mock(DestinationClient.class);
        ItineraryServiceImpl service = new ItineraryServiceImpl(
                itineraryMapper,
                itineraryDestinationMapper,
                destinationClient);

        ItineraryCreateRequest request = ItineraryCreateRequest.builder()
                .userId(10L)
                .title("May trip")
                .destinationIds(List.of(1L, 1L))
                .build();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Destination ids must be unique.", exception.getReason());
        verify(itineraryMapper, never()).insert(any(Itinerary.class));
    }

    @Test
    void createPersistsItineraryAndDestinations() {
        ItineraryMapper itineraryMapper = mock(ItineraryMapper.class);
        ItineraryDestinationMapper itineraryDestinationMapper = mock(ItineraryDestinationMapper.class);
        DestinationClient destinationClient = mock(DestinationClient.class);
        ItineraryServiceImpl service = new ItineraryServiceImpl(
                itineraryMapper,
                itineraryDestinationMapper,
                destinationClient);

        ItineraryCreateRequest request = ItineraryCreateRequest.builder()
                .userId(10L)
                .title("May trip")
                .description("Hangzhou weekend plan")
                .destinationIds(List.of(2L, 3L))
                .build();

        doAnswer(invocation -> {
            Itinerary itinerary = invocation.getArgument(0);
            itinerary.setId(99L);
            return 1;
        }).when(itineraryMapper).insert(any(Itinerary.class));

        when(destinationClient.getById(2L)).thenReturn(ApiResponse.success(destinationSummary(2L, "West Lake")));
        when(destinationClient.getById(3L)).thenReturn(ApiResponse.success(destinationSummary(3L, "Lingyin Temple")));
        when(itineraryMapper.selectById(99L)).thenReturn(Itinerary.builder()
                .id(99L)
                .userId(10L)
                .title("May trip")
                .description("Hangzhou weekend plan")
                .build());
        when(itineraryDestinationMapper.selectByItineraryId(99L)).thenReturn(List.of(
                ItineraryDestination.builder().itineraryId(99L).destinationId(2L).sortOrder(1).build(),
                ItineraryDestination.builder().itineraryId(99L).destinationId(3L).sortOrder(2).build()
        ));

        var response = service.create(request);

        assertEquals(99L, response.getId());
        assertEquals(2, response.getDestinations().size());
        assertEquals("West Lake", response.getDestinations().get(0).getName());
        assertEquals("Lingyin Temple", response.getDestinations().get(1).getName());
        verify(itineraryDestinationMapper, times(2)).insert(any(ItineraryDestination.class));
    }

    @Test
    void addDestinationRejectsDuplicateAssociation() {
        ItineraryMapper itineraryMapper = mock(ItineraryMapper.class);
        ItineraryDestinationMapper itineraryDestinationMapper = mock(ItineraryDestinationMapper.class);
        DestinationClient destinationClient = mock(DestinationClient.class);
        ItineraryServiceImpl service = new ItineraryServiceImpl(
                itineraryMapper,
                itineraryDestinationMapper,
                destinationClient);

        when(itineraryDestinationMapper.selectByItineraryIdAndDestinationId(8L, 5L))
                .thenReturn(ItineraryDestination.builder().itineraryId(8L).destinationId(5L).sortOrder(1).build());

        ItineraryDestinationAddRequest request = ItineraryDestinationAddRequest.builder()
                .destinationId(5L)
                .build();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.addDestination(8L, request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Destination already exists in itinerary.", exception.getReason());
        verify(destinationClient, never()).getById(eq(5L));
    }

    private DestinationSummary destinationSummary(Long id, String name) {
        return DestinationSummary.builder()
                .id(id)
                .name(name)
                .regionCode("330106")
                .regionName("西湖区")
                .summary(name + " summary")
                .coverImageUrl("https://example.com/" + id + ".jpg")
                .build();
    }
}
