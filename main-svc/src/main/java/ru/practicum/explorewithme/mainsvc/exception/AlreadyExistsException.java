package ru.practicum.explorewithme.mainsvc.exception;

import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

public class AlreadyExistsException extends EwmBaseRuntimeException {
    public AlreadyExistsException(ErrorResponseDto response) {
        super(response);
    }
}
