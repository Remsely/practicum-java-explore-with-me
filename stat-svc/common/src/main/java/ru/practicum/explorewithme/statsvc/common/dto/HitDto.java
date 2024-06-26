package ru.practicum.explorewithme.statsvc.common.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class HitDto {
    @NotBlank(message = "Идентификатор сервиса не должен быть пустым.")
    private String app;

    @NotBlank(message = "URI не должен быть пустым.")
    private String uri;

    @NotBlank(message = "IP-адрес пользователя не должен быть пустым.")
    private String ip;

    @NotNull(message = "Дата и время не должны быть пустыми.")
    private LocalDateTime timestamp;
}
