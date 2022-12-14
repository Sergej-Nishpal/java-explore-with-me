package ru.practicum.ewmmain.service.admin;

import ru.practicum.ewmmain.dto.*;
import ru.practicum.ewmmain.dto.incoming.*;
import ru.practicum.ewmmain.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface AdminAccessService {
    List<EventFullDto> getEvents(Set<Long> users, Set<EventState> states, Set<Long> categories,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);

    CategoryDto changeCategory(CategoryDto categoryDto);

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long catId);

    List<UserDto> getUsers(Set<Long> ids, Integer from, Integer size);

    UserDto addUser(NewUserRequest newUserRequest);

    void deleteUserById(Long userId);

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventToCompilation(Long compId, Long eventId);

    void unpinCompilationById(Long compId);

    void pinCompilationById(Long compId);

    List<LocationFullDto> getLocations(Integer from, Integer size);

    LocationFullDto getLocationById(Long locId);

    LocationFullDto addLocation(LocationDto locationDto);

    LocationFullDto changeLocation(Long locId, LocationDto locationDto);

    void deleteLocation(Long locId);
}