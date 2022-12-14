package ru.practicum.ewmmain.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.dto.*;
import ru.practicum.ewmmain.dto.incoming.*;
import ru.practicum.ewmmain.model.EventState;
import ru.practicum.ewmmain.service.admin.AdminAccessService;
import ru.practicum.ewmmain.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
public class AdminAccessController {
    private final AdminAccessService adminAccessService;

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(required = false) Set<Long> users,
                                        @RequestParam(required = false) Set<EventState> states,
                                        @RequestParam(required = false) Set<Long> categories,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        LocalDateTime rangeStart,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        LocalDateTime rangeEnd,
                                        @RequestParam(required = false, defaultValue = "0")
                                        @PositiveOrZero Integer from,
                                        @RequestParam(required = false, defaultValue = "10")
                                        @Positive Integer size) {

        log.debug("Запрос админом информации о событиях.");
        return adminAccessService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Positive Long eventId,
                                    @RequestBody AdminUpdateEventRequest adminUpdateEventRequest) {

        log.debug("Обновление админом события с id = {}.", eventId);
        return adminAccessService.updateEvent(eventId, adminUpdateEventRequest);
    }

    @PatchMapping("/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable @Positive Long eventId) {

        log.debug("Публикация админом события с id = {}.", eventId);
        return adminAccessService.publishEvent(eventId);
    }

    @PatchMapping("/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable @Positive Long eventId) {

        log.debug("Отклонение админом события с id = {}.", eventId);
        return adminAccessService.rejectEvent(eventId);
    }

    @PatchMapping("/categories")
    public CategoryDto changeCategory(@RequestBody @Valid CategoryDto categoryDto) {

        log.debug("Изменение админом категории с id = {}.", categoryDto.getId());
        return adminAccessService.changeCategory(categoryDto);
    }

    @PostMapping("/categories")
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {

        log.debug("Добавление админом новой категории с именем \"{}\".", newCategoryDto.getName());
        return adminAccessService.addCategory(newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategoryById(@PathVariable @Positive Long catId) {

        log.debug("Удаление админом категории с id = {}.", catId);
        adminAccessService.deleteCategoryById(catId);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) Set<Long> ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(defaultValue = "10") @Positive Integer size) {

        log.debug("Запрос админом информации о пользователях.");
        return adminAccessService.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    public UserDto addUser(@RequestBody @Valid NewUserRequest newUserRequest) {

        log.debug("Добавление админом пользователя с именем \"{}\".", newUserRequest.getName());
        return adminAccessService.addUser(newUserRequest);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUserById(@PathVariable @Positive Long userId) {

        log.debug("Удаление админом пользователя с id = {}.", userId);
        adminAccessService.deleteUserById(userId);
    }

    @PostMapping("/compilations")
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {

        log.debug("Добавление админом новой подборки с заголовком \"{}\".", newCompilationDto.getTitle());
        return adminAccessService.addCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilationById(@PathVariable @Positive Long compId) {

        log.debug("Удаление админом подборки с id = {}.", compId);
        adminAccessService.deleteCompilationById(compId);
    }

    @DeleteMapping("/compilations/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable @Positive Long compId,
                                           @PathVariable @Positive Long eventId) {

        log.debug("Удаление админом события с id = {} из подборки с id = {}.", eventId, compId);
        adminAccessService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/compilations/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable @Positive Long compId,
                                      @PathVariable @Positive Long eventId) {

        log.debug("Добавление админом события с id = {} в подборку с id = {}.", eventId, compId);
        adminAccessService.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/compilations/{compId}/pin")
    public void unpinCompilationById(@PathVariable @Positive Long compId) {

        log.debug("Открепление админом подборки с id = {}.", compId);
        adminAccessService.unpinCompilationById(compId);
    }

    @PatchMapping("/compilations/{compId}/pin")
    public void pinCompilationById(@PathVariable @Positive Long compId) {

        log.debug("Закрепление админом подборки с id = {}.", compId);
        adminAccessService.pinCompilationById(compId);
    }

    @GetMapping("/locations")
    public List<LocationFullDto> getLocations(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {

        log.debug("Запрос админом всех локаций из БД.");
        return adminAccessService.getLocations(from, size);
    }

    @GetMapping("/locations/{locId}")
    public LocationFullDto getLocationById(@PathVariable @Positive Long locId) {

        log.debug("Запрос админом локации с id = {}.", locId);
        return adminAccessService.getLocationById(locId);
    }

    @PostMapping("/locations")
    @Validated({Marker.AdminLocation.class})
    public LocationFullDto addLocation(@RequestBody @Valid LocationDto locationDto) {

        log.debug("Добавление админом локации с описанием \"{}\".", locationDto.getDescription());
        return adminAccessService.addLocation(locationDto);
    }

    @PatchMapping("/locations/{locId}")
    @Validated({Marker.AdminLocation.class})
    public LocationFullDto changeLocation(@PathVariable @Positive Long locId,
                                          @RequestBody @Valid LocationDto locationDto) {

        log.debug("Изменение админом локации с id = {}.", locId);
        return adminAccessService.changeLocation(locId, locationDto);
    }

    @DeleteMapping("/locations/{locId}")
    public void deleteLocation(@PathVariable @Positive Long locId) {

        log.debug("Удаление админом локации с id = {}.", locId);
        adminAccessService.deleteLocation(locId);
    }
}