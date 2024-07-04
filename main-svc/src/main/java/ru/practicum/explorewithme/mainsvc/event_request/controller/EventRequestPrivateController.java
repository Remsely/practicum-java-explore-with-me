package ru.practicum.explorewithme.mainsvc.event_request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.event_request.dto.EventRequestDto;
import ru.practicum.explorewithme.mainsvc.event_request.service.EventRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class EventRequestPrivateController {
    private final EventRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventRequestDto postRequest(@RequestParam Long eventId, @PathVariable long userId) {
        log.info("/users/{}/requests?eventId={} POST", userId, eventId);
        return requestService.addRequest(eventId, userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public EventRequestDto cancelRequest(@PathVariable Long requestId, @PathVariable long userId) {
        log.info("/users/{}/requests/{} PATCH", userId, requestId);
        return requestService.cancelRequest(requestId, userId);
    }

    @GetMapping
    public List<EventRequestDto> getRequests(@PathVariable long userId) {
        log.info("/users/{}/requests GET", userId);
        return requestService.getRequestsByUserId(userId);
    }
}
