package ru.practicum.explorewithme.mainsvc.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.request.dto.RequestDto;
import ru.practicum.explorewithme.mainsvc.request.entity.Request;

@Component
public class RequestMapper {
    public RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .build();
    }
}
