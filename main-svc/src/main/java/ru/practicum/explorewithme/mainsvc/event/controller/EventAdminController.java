package ru.practicum.explorewithme.mainsvc.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.TimeRangeRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.EventAdminUpdateDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventsAdminRequests;
import ru.practicum.explorewithme.mainsvc.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    @PatchMapping("/{eventId}")
    public EventDto patchEvent(@RequestBody @Valid EventAdminUpdateDto dto, @PathVariable long eventId) {
        log.info("/admin/events/{} PATCH. Body : {}", eventId, dto);
        eventValidator.validateEventAdminUpdateDto(dto);
        return eventService.updateEventByAdmin(eventId, dto);
    }

    @GetMapping
    public List<EventDto> getEvents(@ModelAttribute @Validated PaginationRequest paginationRequest,
                                    @ModelAttribute @Validated TimeRangeRequest timeRangeRequest,
                                    @ModelAttribute @Validated EventsAdminRequests eventsAdminRequests) {
        log.info("/admin/events?users={}&states={}&categories={}&rangeStart={}&rangeEnd={}&from={}&size={} GET. ",
                eventsAdminRequests.getUsers(), eventsAdminRequests.getStates(), eventsAdminRequests.getCategories(),
                timeRangeRequest.getRangeStart(), timeRangeRequest.getRangeEnd(),
                paginationRequest.getFrom(), paginationRequest.getSize()
        );
        timeRangeRequest.validate();
        return eventService.getEventsByAdmin(paginationRequest, timeRangeRequest, eventsAdminRequests);
    }
}
