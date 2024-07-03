package ru.practicum.explorewithme.mainsvc.request.service;

import ru.practicum.explorewithme.mainsvc.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(long eventId, long userId);

    RequestDto cancelRequest(long requestId, long userId);

    List<RequestDto> getRequestsByUserId(long userId);
}
