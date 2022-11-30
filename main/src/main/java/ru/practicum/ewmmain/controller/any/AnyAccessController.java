package ru.practicum.ewmmain.controller.any;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmmain.dto.CategoryDto;
import ru.practicum.ewmmain.dto.CompilationDto;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;
import ru.practicum.ewmmain.service.any.AnyAccessService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class AnyAccessController {
    private static final String APP = "ewm-main-service";

    private final AnyAccessService anyAccessService;

    @GetMapping("/events")
    public Collection<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) Set<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeStart,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeEnd,
                                               @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(required = false) String sort,
                                               @RequestParam(required = false, defaultValue = "0")
                                               @PositiveOrZero Integer from,
                                               @RequestParam(required = false, defaultValue = "10")
                                               @Positive Integer size,
                                               HttpServletRequest request) {

        final String ip = request.getRemoteAddr();
        final String path = request.getRequestURI();

        log.debug("Запрос информации о событиях с ip: {}.", ip);

        final EventsRequestParameters requestParameters = EventsRequestParameters.builder()
                .text(text)
                .categoryIds(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        return anyAccessService.getEvents(requestParameters, ip, path, APP);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventById(@PathVariable @Positive Long eventId, HttpServletRequest request) {
        final String ip = request.getRemoteAddr();
        final String path = request.getRequestURI();
        log.debug("Получение информации о событии с id = {}, запрос с ip: {}.", eventId, ip);
        return anyAccessService.getEventById(eventId, ip, path, APP);
    }

    @GetMapping("/compilations")
    public Collection<CompilationDto> getCompilations(@RequestParam Boolean pinned,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.debug("Получение подборок событий.");
        return anyAccessService.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable @Positive Long compId) {
        log.debug("Получение информации о подборке событий с id = {}.", compId);
        return anyAccessService.getCompilationById(compId);
    }

    @GetMapping("/categories")
    public Collection<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.debug("Получение списка категорий.");
        return anyAccessService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable("catId") @Positive Long catId) {
        log.debug("Получение категории с id = {}.", catId);
        return anyAccessService.getCategoryById(catId);
    }
}