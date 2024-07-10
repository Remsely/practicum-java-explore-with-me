package ru.practicum.explorewithme.statsvc.common.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@Data
@Builder
public class StatsRequest {
    @NotNull(message = "Start must not be null.")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;

    @NotNull(message = "End must not be null.")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;

    private List<String> uris;

    private Boolean unique;

    public void validate() {
        if (end.isBefore(start)) {
            throw new ValidationException("End must be after start.");
        }
    }
}
