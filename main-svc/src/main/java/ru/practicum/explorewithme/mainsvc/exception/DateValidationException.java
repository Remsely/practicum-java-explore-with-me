package ru.practicum.explorewithme.mainsvc.exception;

import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

public class DateValidationException extends EwmBaseRuntimeException {
    public DateValidationException(ErrorResponseDto response) {
        super(response);
    }
}
