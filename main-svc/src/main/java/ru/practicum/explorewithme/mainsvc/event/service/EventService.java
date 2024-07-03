package ru.practicum.explorewithme.mainsvc.event.service;

import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.*;

import java.util.List;

public interface EventService {
    EventDto addEvent(EventCreationDto dto, long userId);

    EventDto updateEventByUser(long eventId, EventUserUpdateDto dto, long userId);

    EventDto updateEventByAdmin(long eventId, EventAdminUpdateDto dto);

    EventDto getEventById(long eventId, long userId);

    List<EventShortDto> getEventsByUser(long userId, PaginationRequest paginationRequest);
}
