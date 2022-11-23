package ru.practicum.ewmmain.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.dto.CategoryDto;
import ru.practicum.ewmmain.dto.CompilationDto;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.UserDto;
import ru.practicum.ewmmain.dto.incoming.AdminUpdateEventRequest;
import ru.practicum.ewmmain.dto.incoming.NewCategoryDto;
import ru.practicum.ewmmain.dto.incoming.NewCompilationDto;
import ru.practicum.ewmmain.dto.incoming.NewUserRequest;
import ru.practicum.ewmmain.model.EventState;
import ru.practicum.ewmmain.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminAccessServiceImpl implements AdminAccessService {
    private final EventRepository eventRepository;

    @Override
    public Collection<EventFullDto> getEvents(Set<Long> users, Set<EventState> states,
                                              Set<Integer> categories, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Integer from, Integer size) {
        return Collections.emptyList();
    }

    @Override
    public EventFullDto updateEvent(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {
        return null;
    }

    @Override
    public EventFullDto publishEvent(Long eventId) {
        return null;
    }

    @Override
    public EventFullDto rejectEvent(Long eventId) {
        return null;
    }

    @Override
    public CategoryDto changeCategory(CategoryDto categoryDto) {
        return null;
    }

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        return null;
    }

    @Override
    public void deleteCategoryById(Long catId) {

    }

    @Override
    public Collection<UserDto> getUsers(Set<Long> ids, Integer from, Integer size) {
        return Collections.emptyList();
    }

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        return null;
    }

    @Override
    public void deleteUserById(Long userId) {

    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        return null;
    }

    @Override
    public void deleteCompilationById(Long compId) {

    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {

    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) {

    }

    @Override
    public void unpinCompilationById(Long compId) {

    }

    @Override
    public void pinCompilationById(Long compId) {

    }
}
