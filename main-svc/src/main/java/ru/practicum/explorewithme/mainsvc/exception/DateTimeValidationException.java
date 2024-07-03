package ru.practicum.explorewithme.mainsvc.exception;

import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

public class DateTimeValidationException extends EwmBaseRuntimeException {
    public DateTimeValidationException(ErrorResponseDto response) {
        super(response);
    }
}
