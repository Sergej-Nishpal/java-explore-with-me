package ru.practicum.ewmmain.service.any;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.controller.any.EventSortType;
import ru.practicum.ewmmain.controller.any.EventsRequestParameters;
import ru.practicum.ewmmain.dto.CategoryDto;
import ru.practicum.ewmmain.dto.CompilationDto;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;
import ru.practicum.ewmmain.model.Event;
import ru.practicum.ewmmain.model.EventState;
import ru.practicum.ewmmain.model.QEvent;
import ru.practicum.ewmmain.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Slf4j //TODO Логирование!
@Service
@RequiredArgsConstructor
public class AnyAccessServiceImpl implements AnyAccessService {
    private final EventRepository eventRepository;
    @Override
    public Collection<EventShortDto> getEvents(EventsRequestParameters parameters, String ip,
                                               String path, String appName) {
        //это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
        //текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
        //если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
        //информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики

        final QEvent event = QEvent.event;

        final Predicate eventDatePredicate = parameters.getRangeStart() == null && parameters.getRangeEnd() == null
                ? event.eventDate.after(LocalDateTime.now())
                : event.eventDate.between(parameters.getRangeStart(), parameters.getRangeEnd());

        final Predicate predicate = (event.annotation.containsIgnoreCase(parameters.getText())
                .or(event.annotation.containsIgnoreCase(parameters.getText())))
                .and(event.category.id.in(parameters.getCategoryIds()))
                .and(event.paid.eq(parameters.getPaid()))
                .and(event.state.eq(EventState.PUBLISHED))
                .and(eventDatePredicate)
                .and(event.confirmedRequests.lt(event.participantLimit));

        if (parameters.getSort().equals(EventSortType.EVENT_DATE)) {
            final Pageable pageable = PageRequest.of(parameters.getFrom() / parameters.getSize(),
                    parameters.getSize(), Sort.by("eventDate"));
            Page<Event> events = eventRepository.findAll(predicate, pageable);
            // TODO Тут дальше надо клиента статистики для получения views и сохранения запроса.
        }



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