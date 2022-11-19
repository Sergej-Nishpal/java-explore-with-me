package ru.practicum.ewmmain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.controller.any.EventsRequestParameters;
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

        return Collections.emptyList();
    }

    @Override
    public EventFullDto getEventById(Long eventId, String ip, String path, String appName) {
        return null;
    }
}