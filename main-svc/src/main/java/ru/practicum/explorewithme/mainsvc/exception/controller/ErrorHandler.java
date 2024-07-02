package ru.practicum.explorewithme.mainsvc.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.mainsvc.exception.AlreadyExistsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.exception.EwmBaseRuntimeException;
import ru.practicum.explorewithme.mainsvc.exception.IllegalPageableArgumentsException;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleEntityNotFound(EntityNotFoundException e) {
        return logMessageAndGetResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleIllegalPageableArguments(IllegalPageableArgumentsException e) {
        return logMessageAndGetResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleAlreadyExists(AlreadyExistsException e) {
        return logMessageAndGetResponse(e);
    }

    private ErrorResponseDto logMessageAndGetResponse(EwmBaseRuntimeException e) {
        ErrorResponseDto errorResponseDto = e.getResponse();
        log.warn(errorResponseDto.getMessage());
        return errorResponseDto;
    }
}
