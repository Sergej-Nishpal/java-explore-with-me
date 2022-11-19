package ru.practicum.ewmmain.service;

import ru.practicum.ewmmain.controller.any.EventsRequestParameters;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;

import java.util.Collection;

public interface AnyAccessService {
    Collection<EventShortDto> getEvents(EventsRequestParameters parameters, String ip, String path, String appName);

    EventFullDto getEventById(Long eventId, String ip, String path, String appName);
}
