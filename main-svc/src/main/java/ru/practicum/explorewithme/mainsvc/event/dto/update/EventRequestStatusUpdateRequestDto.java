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
    @Size(min = 1)
    private List<Long> requestIds;

    @NotNull
    private EventRequestStatus status;
}
