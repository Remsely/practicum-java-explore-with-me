package ru.practicum.explorewithme.mainsvc.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.mainsvc.request.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class RequestDto {
    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;
    private RequestStatus status;
}
