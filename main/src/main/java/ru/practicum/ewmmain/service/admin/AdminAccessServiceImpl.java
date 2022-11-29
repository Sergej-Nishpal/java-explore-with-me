package ru.practicum.ewmmain.service.admin;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.CategoryDto;
import ru.practicum.ewmmain.dto.CompilationDto;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.UserDto;
import ru.practicum.ewmmain.dto.incoming.AdminUpdateEventRequest;
import ru.practicum.ewmmain.dto.incoming.NewCategoryDto;
import ru.practicum.ewmmain.dto.incoming.NewCompilationDto;
import ru.practicum.ewmmain.dto.incoming.NewUserRequest;
import ru.practicum.ewmmain.dto.mapper.EntityMapper;
import ru.practicum.ewmmain.exception.*;
import ru.practicum.ewmmain.model.*;
import ru.practicum.ewmmain.repository.CategoryRepository;
import ru.practicum.ewmmain.repository.CompilationRepository;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAccessServiceImpl implements AdminAccessService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CompilationRepository compilationRepository;

    @Override
    public Collection<EventFullDto> getEvents(Set<Long> users, Set<EventState> states,
                                              Set<Long> categories, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Integer from, Integer size) {
        final QEvent event = QEvent.event;
        final Predicate predicate = event.initiator.id.in(users)
                .and(event.state.in(states))
                .and(event.category.id.in(categories))
                .and(event.eventDate.between(rangeStart, rangeEnd));
        final Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAll(predicate, pageable)
                .stream()
                .map(EntityMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {

        return null;
    }

    @Override
    public EventFullDto publishEvent(Long eventId) {
        final int hoursBeforeForPublish = 1;
        // дата начала события должна быть не ранее чем за час от даты публикации.
        // событие должно быть в состоянии ожидания публикации
        Event event = getEventWithCheck(eventId);
        if (!event.getEventDate().minusHours(hoursBeforeForPublish).isAfter(LocalDateTime.now())) {
            throw new IncorrectEventDateException(hoursBeforeForPublish);
        }

        if (!event.getState().equals(EventState.PENDING)) {
            throw new EventStateException("Событие должно быть в состоянии ожидания публикации!");
        }

        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        return EntityMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(Long eventId) {
        //событие не должно быть опубликовано
        Event event = getEventWithCheck(eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateException("Событие не должно быть опубликовано!");
        }

        event.setState(EventState.CANCELED);
        return EntityMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public CategoryDto changeCategory(CategoryDto categoryDto) {
        //имя категории должно быть уникальным
        Category category = getCategoryWithCheck(categoryDto.getId());
        category.setName(category.getName());
        return EntityMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        final Category category = categoryRepository.save(EntityMapper.toCategory(newCategoryDto));
        return EntityMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long catId) {
        categoryRepository.deleteById(catId);
    }

    @Override
    public Collection<UserDto> getUsers(Set<Long> ids, Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(from / size, size);
        return userRepository.findAllByIdIn(ids, pageable)
                .stream()
                .map(EntityMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        final User user = userRepository.save(EntityMapper.toUser(newUserRequest));
        return EntityMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        final Collection<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        final Set<Event> eventSet = new HashSet<>(events);
        final Compilation compilation = compilationRepository
                .save(EntityMapper.toCompilation(newCompilationDto, eventSet));
        return EntityMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        final Compilation compilation = getCompilationWithCheck(compId);
        final Event event = getEventWithCheck(eventId);
        final Set<Event> events = compilation.getEvents();
        events.remove(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void addEventToCompilation(Long compId, Long eventId) {
        final Compilation compilation = getCompilationWithCheck(compId);
        final Event event = getEventWithCheck(eventId);
        final Set<Event> events = compilation.getEvents();
        events.add(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void unpinCompilationById(Long compId) {
        Compilation compilation = getCompilationWithCheck(compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void pinCompilationById(Long compId) {
        Compilation compilation = getCompilationWithCheck(compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    private Category getCategoryWithCheck(long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("Категория с id = {} не найдена!", categoryId);
            throw new CategoryNotFoundException(categoryId);
        });
    }

    private Event getEventWithCheck(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Событие с id = {} не найдено!", eventId);
            throw new EventNotFoundException(eventId);
        });
    }

    private Compilation getCompilationWithCheck(long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> {
            log.error("Подборка с id = {} не найдена!", compId);
            throw new CompilationNotFoundException(compId);
        });
    }
}
