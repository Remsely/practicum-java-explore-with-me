package ru.practicum.explorewithme.mainsvc.exception;

public class LocationValidationException extends RuntimeException {
    public LocationValidationException(String message) {
        super(message);
    }
}
