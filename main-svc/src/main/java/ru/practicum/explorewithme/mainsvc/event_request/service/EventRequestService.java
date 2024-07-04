package ru.practicum.explorewithme.mainsvc.event_request.service;

import ru.practicum.explorewithme.mainsvc.event_request.dto.EventRequestDto;

import java.util.List;

public interface EventRequestService {
    EventRequestDto addRequest(long eventId, long userId);

    EventRequestDto cancelRequest(long requestId, long userId);

    List<EventRequestDto> getRequestsByUserId(long userId);
}
