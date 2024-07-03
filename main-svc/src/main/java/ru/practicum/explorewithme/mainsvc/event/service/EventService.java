package ru.practicum.explorewithme.mainsvc.event.service;

import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.EventCreationDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventShortDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventUpdateDto;

import java.util.List;

public interface EventService {
    EventDto addEvent(EventCreationDto dto, long userId);

    EventDto updateEvent(long eventId, EventUpdateDto dto, long userId);

    EventDto getEventById(long eventId, long userId);

    List<EventShortDto> getEventsByUser(long userId, PaginationRequest paginationRequest);
}
