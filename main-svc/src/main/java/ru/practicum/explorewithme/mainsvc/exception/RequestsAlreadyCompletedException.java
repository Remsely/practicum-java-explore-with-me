package ru.practicum.explorewithme.mainsvc.exception;

public class RequestsAlreadyCompletedException extends RuntimeException {
    public RequestsAlreadyCompletedException(String message) {
        super(message);
    }
}
