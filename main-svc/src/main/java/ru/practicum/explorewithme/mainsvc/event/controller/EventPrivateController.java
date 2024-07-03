package ru.practicum.explorewithme.mainsvc.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.EventCreationDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventShortDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventUpdateDto;
import ru.practicum.explorewithme.mainsvc.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto postEvent(@RequestBody @Valid EventCreationDto eventDto, @PathVariable long userId) {
        log.info("POST /users/{}/events. Body : {}", userId, eventDto);
        eventValidator.validateEventCreationDto(eventDto);
        return eventService.addEvent(eventDto, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDto patchEvent(@RequestBody @Valid EventUpdateDto eventDto,
                               @PathVariable long userId, @PathVariable long eventId) {
        log.info("PATCH /users/{}/events/{}. Body : {}", userId, eventId, eventDto);
        eventValidator.validateEventUpdateDto(eventDto);
        return eventService.updateEvent(eventId, eventDto, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDto getEvent(@PathVariable long userId, @PathVariable long eventId) {
        log.info("GET /users/{}/events/{}", userId, eventId);
        return eventService.getEventById(eventId, userId);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsByUser(@PathVariable long userId,
                                               @ModelAttribute @Validated PaginationRequest request) {
        log.info("GET /users/{}/events?from={}&size={}", userId, request.getFrom(), request.getSize());
        return eventService.getEventsByUser(userId, request);
    }
}
