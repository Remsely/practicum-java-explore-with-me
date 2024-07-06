package ru.practicum.explorewithme.mainsvc.exception;

public class NotPublicException extends RuntimeException {
    public NotPublicException(String message) {
        super(message);
    }
}
