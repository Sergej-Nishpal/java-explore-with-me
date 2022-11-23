package ru.practicum.ewmmain.service.admin;

import ru.practicum.ewmmain.dto.CategoryDto;
import ru.practicum.ewmmain.dto.CompilationDto;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.UserDto;
import ru.practicum.ewmmain.dto.incoming.AdminUpdateEventRequest;
import ru.practicum.ewmmain.dto.incoming.NewCategoryDto;
import ru.practicum.ewmmain.dto.incoming.NewCompilationDto;
import ru.practicum.ewmmain.dto.incoming.NewUserRequest;
import ru.practicum.ewmmain.model.EventState;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface AdminAccessService {
    Collection<EventFullDto> getEvents(Set<Long> users, Set<EventState> states, Set<Integer> categories,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);

    CategoryDto changeCategory(CategoryDto categoryDto);

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long catId);

    Collection<UserDto> getUsers(Set<Long> ids, Integer from, Integer size);

    UserDto addUser(NewUserRequest newUserRequest);

    void deleteUserById(Long userId);

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventToCompilation(Long compId, Long eventId);

    void unpinCompilationById(Long compId);

    void pinCompilationById(Long compId);
}
