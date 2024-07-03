package ru.practicum.explorewithme.mainsvc.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.mainsvc.request.entity.RequestStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class RequestStatusUpdateRequestDto {
    @Size(min = 1)
    private List<Long> requestIds;

    @NotNull
    private RequestStatus status;
}
