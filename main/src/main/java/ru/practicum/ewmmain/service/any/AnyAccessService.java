package ru.practicum.ewmmain.service.any;

import ru.practicum.ewmmain.controller.any.EventsRequestParameters;
import ru.practicum.ewmmain.dto.CategoryDto;
import ru.practicum.ewmmain.dto.CompilationDto;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;

import java.util.Collection;

public interface AnyAccessService {
    Collection<EventShortDto> getEvents(EventsRequestParameters parameters, String ip, String path, String appName);

    EventFullDto getEventById(Long eventId, String ip, String path, String appName);

    Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);

    Collection<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);
}
