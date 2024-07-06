package ru.practicum.explorewithme.mainsvc.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.creation.EventCreationDto;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventFullDto;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventShortDto;
import ru.practicum.explorewithme.mainsvc.event.dto.update.EventRequestStatusUpdateRequestDto;
import ru.practicum.explorewithme.mainsvc.event.dto.update.EventRequestStatusUpdateResultDto;
import ru.practicum.explorewithme.mainsvc.event.dto.update.EventUserUpdateDto;
import ru.practicum.explorewithme.mainsvc.event.service.EventService;
import ru.practicum.explorewithme.mainsvc.event_request.dto.EventRequestDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@RequestBody @Valid EventCreationDto eventDto, @PathVariable long userId) {
        log.info("Crete event by user with id {} (/users/{}/events POST). Body : {}", userId, userId, eventDto);
        eventValidator.validateEventCreationDto(eventDto);
        return eventService.addEvent(eventDto, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(@RequestBody @Valid EventUserUpdateDto eventDto,
                                   @PathVariable long userId, @PathVariable long eventId) {
        log.info("Update event with id {} by user with id {} (/users/{}/events/{} PATCH). Body : {}",
                eventId, userId, userId, eventId, eventDto);
        eventValidator.validateEventUserUpdateDto(eventDto);
        return eventService.updateEventByUser(eventId, eventDto, userId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Get event with id {} by user with id {} (/users/{}/events/{} GET).",
                eventId, userId, userId, eventId);
        return eventService.getUserEventById(eventId, userId);
    }

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable long userId,
                                               @ModelAttribute @Validated PaginationRequest request) {
        log.info("Get events by user with id {} (/users/{}/events?from={}&size={} GET).",
                userId, userId, request.getFrom(), request.getSize());
        return eventService.getEventsByUser(userId, request);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto patchEventRequest(@RequestBody @Valid EventRequestStatusUpdateRequestDto request,
                                                               @PathVariable long userId, @PathVariable long eventId) {
        log.info("Update event requests status by user with id {} (/users/{}/events/{}/requests PATCH). Body : {}",
                userId, userId, eventId, request);
        eventValidator.validateEventRequestStatusUpdateRequest(request);
        return eventService.updateEventRequestsByUser(eventId, request, userId);
    }

    @GetMapping("/{eventId}/requests")
    public List<EventRequestDto> getEventRequests(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Get event requests by user with id {} (/users/{}/events/{}/requests GET).", userId, userId, eventId);
        return eventService.getEventRequestsByUser(eventId, userId);
    }
}
