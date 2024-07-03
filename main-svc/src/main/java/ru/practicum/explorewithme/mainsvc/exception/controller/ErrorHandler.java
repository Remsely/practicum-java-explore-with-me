package ru.practicum.explorewithme.mainsvc.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.mainsvc.exception.*;
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
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleAlreadyExists(AlreadyExistsException e) {
        return logMessageAndGetResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleDateValidationException(DateTimeValidationException e) {
        return logMessageAndGetResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleAccessRights(AccessRightsException e) {
        return logMessageAndGetResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleRequestsAlreadyCompleted(RequestsAlreadyCompletedException e) {
        return logMessageAndGetResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleRequestsAlreadyCompleted(IllegalStatusException e) {
        return logMessageAndGetResponse(e);
    }

    private ErrorResponseDto logMessageAndGetResponse(EwmBaseRuntimeException e) {
        ErrorResponseDto response = e.getResponse();
        log.warn("{} : {}", response.getStatus(), response.getMessage());
        return response;
    }
}
