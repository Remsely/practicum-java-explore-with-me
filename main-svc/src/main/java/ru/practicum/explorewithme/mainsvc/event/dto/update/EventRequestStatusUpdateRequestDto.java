package ru.practicum.explorewithme.mainsvc.event.dto.update;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequestDto {
    @NotNull(message = "Request ids should not be undefined.")
    @Size(min = 1, message = "Request ids should not be empty.")
    private List<Long> requestIds;

    @NotNull(message = "Request status should not be undefined.")
    private EventRequestStatus status;
}
