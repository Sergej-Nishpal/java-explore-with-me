package ru.practicum.ewmmain.service.any;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.controller.any.EventsRequestParameters;
import ru.practicum.ewmmain.dto.CategoryDto;
import ru.practicum.ewmmain.dto.CompilationDto;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;
import ru.practicum.ewmmain.repository.EventRepository;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AnyAccessServiceImpl implements AnyAccessService {
    private final EventRepository eventRepository;
    @Override
    public Collection<EventShortDto> getEvents(EventsRequestParameters parameters, String ip,
                                               String path, String appName) {

        // TODO:
        // - формирование параметров
        // - запрос к базе по параметрам
        // - сохранение запроса в статистику
        return Collections.emptyList();
    }

    @Override
    public EventFullDto getEventById(Long eventId, String ip, String path, String appName) {
        // TODO:
        // - запрос к базе по id
        // - сохранение запроса в статистику
        return null;
    }

    @Override
    public Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        // TODO:
        // - формирование параметров
        // - запрос к базе по параметрам
        return null;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        // TODO:
        // - запрос к базе по id
        return null;
    }

    @Override
    public Collection<CategoryDto> getCategories(Integer from, Integer size) {
        // TODO:
        // - формирование параметров
        // - запрос к базе по параметрам
        return null;
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        // TODO:
        // - запрос к базе по id
        return null;
    }
}