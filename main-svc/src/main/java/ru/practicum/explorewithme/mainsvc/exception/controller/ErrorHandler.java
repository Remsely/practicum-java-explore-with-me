package ru.practicum.explorewithme.mainsvc.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.mainsvc.exception.*;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleDateValidationException(DateTimeValidationException e) {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("Date time validation error.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        logMessage(response);
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleIllegalStatus(IllegalStatusException e) {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("Illegal status error.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        logMessage(response);
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleEntityNotFound(EntityNotFoundException e) {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .reason("Entity not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        logMessage(response);
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotPublic(NotPublicException e) {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .reason("No public information found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        logMessage(response);
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleAlreadyExists(AlreadyExistsException e) {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("Entity already exists.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        logMessage(response);
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleAccessRights(AccessRightsException e) {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("Access denied.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        logMessage(response);
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleRequestsAlreadyCompleted(RequestsAlreadyCompletedException e) {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("Request already completed.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        logMessage(response);
        return response;
    }

    private void logMessage(ErrorResponseDto e) {
        log.warn("{} : {}", e.getStatus(), e.getMessage());
    }
}
