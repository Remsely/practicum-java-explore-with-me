package ru.practicum.explorewithme.mainsvc.event_request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class EventRequestDto {
    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;
    private EventRequestStatus status;
}
