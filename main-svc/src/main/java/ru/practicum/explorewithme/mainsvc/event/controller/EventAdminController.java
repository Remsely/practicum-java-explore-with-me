package ru.practicum.explorewithme.mainsvc.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.event.dto.EventAdminUpdateDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventDto;
import ru.practicum.explorewithme.mainsvc.event.service.EventService;

import javax.validation.Valid;

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
}
