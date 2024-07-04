package ru.practicum.explorewithme.mainsvc.event.dto.update;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.mainsvc.event_request.dto.EventRequestDto;

import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateResultDto {
    private List<EventRequestDto> confirmedRequests;
    private List<EventRequestDto> rejectedRequests;
}
