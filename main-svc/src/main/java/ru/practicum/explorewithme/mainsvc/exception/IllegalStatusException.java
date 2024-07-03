package ru.practicum.explorewithme.mainsvc.exception;

import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

public class IllegalStatusException extends EwmBaseRuntimeException {
    public IllegalStatusException(ErrorResponseDto response) {
        super(response);
    }
}
