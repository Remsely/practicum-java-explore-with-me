package ru.practicum.explorewithme.mainsvc.event.service;

import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.*;
import ru.practicum.explorewithme.mainsvc.request.dto.RequestDto;

import java.util.List;

public interface EventService {
    EventDto addEvent(EventCreationDto dto, long userId);

    EventDto updateEventByUser(long eventId, EventUserUpdateDto dto, long userId);

    EventDto updateEventByAdmin(long eventId, EventAdminUpdateDto dto);

    EventDto getEventById(long eventId, long userId);

    List<EventShortDto> getEventsByUser(long userId, PaginationRequest paginationRequest);

    List<RequestDto> getEventRequestsByUser(long eventId, long userId);

    RequestStatusUpdateResult updateEventRequestsByUser(long eventId, RequestStatusUpdateRequest request, long userId);
}
