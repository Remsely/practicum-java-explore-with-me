package ru.practicum.explorewithme.mainsvc.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.request.dto.RequestDto;
import ru.practicum.explorewithme.mainsvc.request.service.RequestService;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestPrivateController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto postRequest(@RequestParam Long eventId, @PathVariable long userId) {
        log.info("/users/{}/requests?eventId={} POST", userId, eventId);
        return requestService.addRequest(eventId, userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long requestId, @PathVariable long userId) {
        log.info("/users/{}/requests/{} PATCH", userId, requestId);
        return requestService.cancelRequest(requestId, userId);
    }
}
