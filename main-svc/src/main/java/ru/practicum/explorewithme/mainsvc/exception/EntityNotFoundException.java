package ru.practicum.explorewithme.mainsvc.exception;

import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

public class EntityNotFoundException extends EwmBaseRuntimeException {
    public EntityNotFoundException(ErrorResponseDto response) {
        super(response);
    }
}
