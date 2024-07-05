package ru.practicum.explorewithme.mainsvc.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.TimeRangeRequest;
import ru.practicum.explorewithme.mainsvc.common.stat.client.StatClientService;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventFullDto;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventShortDto;
import ru.practicum.explorewithme.mainsvc.event.dto.requests.EventsPublicRequest;
import ru.practicum.explorewithme.mainsvc.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventService;
    private final StatClientService statClientService;

    @GetMapping("/{eventId}")
    public EventFullDto getPublicEvent(@PathVariable long eventId, HttpServletRequest request) {
        log.info("GET /events/{}", eventId);
        EventFullDto event = eventService.getPublicEventById(eventId);
        statClientService.sendStat(request);
        return event;
    }

    @GetMapping
    public List<EventShortDto> getPublicEvents(@ModelAttribute @Validated PaginationRequest paginationRequest,
                                               @ModelAttribute @Validated TimeRangeRequest timeRangeRequest,
                                               @ModelAttribute @Validated EventsPublicRequest eventsPublicRequest,
                                               HttpServletRequest httpServletRequest) {
        timeRangeRequest.validate();
        log.info("GET /events?text={}&categories={}&paid={}&onlyAvailable={}&sort={}&rangeStart={}&rangeEnd={}" +
                        "&from={}&size={}", eventsPublicRequest.getText(), eventsPublicRequest.getCategories(),
                eventsPublicRequest.getPaid(), eventsPublicRequest.getOnlyAvailable(), eventsPublicRequest.getSort(),
                timeRangeRequest.getRangeStart(), timeRangeRequest.getRangeEnd(),
                paginationRequest.getFrom(), paginationRequest.getSize()
        );
        List<EventShortDto> events = eventService.getPublicEvents(paginationRequest, timeRangeRequest, eventsPublicRequest);
        statClientService.sendStat(httpServletRequest);
        return events;
    }
}
