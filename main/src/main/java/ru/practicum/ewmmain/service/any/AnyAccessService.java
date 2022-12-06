package ru.practicum.ewmmain.service.any;

import ru.practicum.ewmmain.controller.any.EventsRequestParameters;
import ru.practicum.ewmmain.dto.*;
import ru.practicum.ewmmain.dto.incoming.GeoData;

import java.util.List;

public interface AnyAccessService {
    List<EventShortDto> getEvents(EventsRequestParameters parameters, EndpointHitDto endpointHitDto);

    List<EventShortDto> getEventsNearLocation(GeoData geoData, Integer from, Integer size, EndpointHitDto endpointHitDto);

    EventFullDto getEventById(Long eventId, String ip, String path, String appName);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);
}