package ru.practicum.explorewithme.statsvc.common.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class HitDto {
    @NotBlank(message = "App name must not be blank.")
    private String app;

    @NotBlank(message = "URI must not be blank.")
    private String uri;

    @NotBlank(message = "IP-address must not be blank.")
    private String ip;

    @NotNull(message = "Date and time must not be null.")
    private LocalDateTime timestamp;
}
