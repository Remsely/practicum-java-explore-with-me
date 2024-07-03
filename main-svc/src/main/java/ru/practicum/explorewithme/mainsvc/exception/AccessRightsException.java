package ru.practicum.explorewithme.mainsvc.exception;

import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

public class AccessRightsException extends EwmBaseRuntimeException {
    public AccessRightsException(ErrorResponseDto response) {
        super(response);
    }
}
