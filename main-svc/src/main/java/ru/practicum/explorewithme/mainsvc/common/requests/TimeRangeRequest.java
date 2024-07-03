package ru.practicum.explorewithme.mainsvc.common.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import ru.practicum.explorewithme.mainsvc.exception.DateTimeValidationException;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

import java.time.LocalDateTime;

@Validated
@Getter
@Setter
public class TimeRangeRequest {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    public void validate() {
        if (rangeStart == null || rangeEnd == null) {
            return;
        }
        if (rangeStart.isAfter(rangeEnd) || rangeStart.isEqual(rangeEnd)) {
            throw new DateTimeValidationException(ErrorResponseDto.builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .reason("Invalid time range")
                    .message("Start time = " + rangeStart + " should be before the end time = " + rangeEnd + " .")
                    .build()
            );
        }
    }
}
