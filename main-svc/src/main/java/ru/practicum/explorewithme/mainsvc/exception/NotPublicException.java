package ru.practicum.explorewithme.mainsvc.exception;

import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

public class NotPublicException extends EwmBaseRuntimeException {
    public NotPublicException(ErrorResponseDto response) {
        super(response);
    }
}
