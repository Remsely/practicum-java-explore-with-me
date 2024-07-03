package ru.practicum.explorewithme.mainsvc.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.mainsvc.request.dto.RequestDto;

import java.util.List;

@Data
@Builder
public class RequestStatusUpdateResult {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
