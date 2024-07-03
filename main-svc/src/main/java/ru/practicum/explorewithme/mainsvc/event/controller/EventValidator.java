package ru.practicum.explorewithme.mainsvc.event.controller;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.event.dto.EventCreationDto;
import ru.practicum.explorewithme.mainsvc.exception.DateValidationException;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

import java.time.LocalDateTime;

@Component
public class EventValidator {
    public void validateEventCreationDto(EventCreationDto eventCreationDto) {
        validateEventDate(eventCreationDto);
        validateRequestModeration(eventCreationDto);
        validateParticipantLimit(eventCreationDto);
        validatePaid(eventCreationDto);
    }

    private void validateEventDate(EventCreationDto eventCreationDto) {
        LocalDateTime eventDate = eventCreationDto.getEventDate();
        if (!eventDate.isAfter(LocalDateTime.now().plusHours(2))) {
            throw new DateValidationException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .reason("Incorrectly made request.")
                            .message("Field: eventDate. Error: event date must be at least 2 hours in the future. " +
                                    "Value: " + eventDate)
                            .build()
            );
        }
    }

    private void validateRequestModeration(EventCreationDto eventCreationDto) {
        Boolean requestModeration = eventCreationDto.getRequestModeration();
        eventCreationDto.setRequestModeration(requestModeration != null ? requestModeration : true);
    }

    private void validateParticipantLimit(EventCreationDto eventCreationDto) {
        Integer participantLimit = eventCreationDto.getParticipantLimit();
        eventCreationDto.setParticipantLimit(participantLimit != null ? participantLimit : 0);
    }

    private void validatePaid(EventCreationDto eventCreationDto) {
        Boolean paid = eventCreationDto.getPaid();
        eventCreationDto.setPaid(paid != null ? paid : false);
    }
}
