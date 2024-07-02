package ru.practicum.explorewithme.mainsvc.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

@Getter
@RequiredArgsConstructor
public class EntityNotFoundException extends RuntimeException {
    private final ErrorResponseDto response;
}
