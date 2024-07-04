package ru.practicum.explorewithme.mainsvc.event_request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.event_request.dto.EventRequestDto;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequest;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventRequestMapper {
    public EventRequestDto toDto(EventRequest request) {
        return EventRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .build();
    }

    public List<EventRequestDto> toDtoList(List<EventRequest> requests) {
        return requests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
