package ru.practicum.explorewithme.mainsvc.exception;

import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

public class RequestsAlreadyCompletedException extends EwmBaseRuntimeException {
    public RequestsAlreadyCompletedException(ErrorResponseDto response) {
        super(response);
    }
}
