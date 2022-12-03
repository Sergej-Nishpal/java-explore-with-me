package ru.practicum.ewmmain.service.any;

import ru.practicum.ewmmain.controller.any.EventsRequestParameters;
import ru.practicum.ewmmain.dto.CategoryDto;
import ru.practicum.ewmmain.dto.CompilationDto;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;

import java.util.List;

public interface AnyAccessService {
    List<EventShortDto> getEvents(EventsRequestParameters parameters, String ip, String path, String appName);

    EventFullDto getEventById(Long eventId, String ip, String path, String appName);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);
}