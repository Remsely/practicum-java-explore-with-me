package ru.practicum.explorewithme.mainsvc.event.controller;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.event.dto.EventAdminUpdateDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventCreationDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventUserUpdateDto;
import ru.practicum.explorewithme.mainsvc.event.dto.RequestStatusUpdateRequestDto;
import ru.practicum.explorewithme.mainsvc.exception.DateTimeValidationException;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;
import ru.practicum.explorewithme.mainsvc.request.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventValidator {
    public void validateEventCreationDto(EventCreationDto eventCreationDto) {
        checkDateAfterPlusHours(eventCreationDto.getEventDate(), 2, false);
        setDefaultRequestModeration(eventCreationDto);
        setDefaultParticipantLimit(eventCreationDto);
        setDefaultPaid(eventCreationDto);
    }

    public void validateEventUserUpdateDto(EventUserUpdateDto dto) {
        checkDateAfterPlusHours(dto.getEventDate(), 2, true);
    }

    public void validateEventAdminUpdateDto(EventAdminUpdateDto dto) {
        checkDateAfterPlusHours(dto.getEventDate(), 1, true);
    }

    public void validateEventRequestStatusUpdateRequest(RequestStatusUpdateRequestDto request) {
        checkStatusIn(request.getStatus(), List.of(RequestStatus.CONFIRMED, RequestStatus.REJECTED));
    }

    private void checkStatusIn(RequestStatus status, List<RequestStatus> statuses) {
        if (!statuses.contains(status)) {
            throw new DateTimeValidationException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .reason("Incorrectly made request.")
                            .message("Field: status. Error: status must be one of: " + statuses)
                            .build()
            );
        }
    }

    private void checkDateAfterPlusHours(LocalDateTime eventDate, int hours, boolean nullable) {
        if (eventDate == null && nullable) {
            return;
        }
        if (eventDate == null) {
            throw new DateTimeValidationException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .reason("Incorrectly made request.")
                            .message("Field: eventDate. Error: event date must be specified.")
                            .build()
            );
        }
        if (!eventDate.isAfter(LocalDateTime.now().plusHours(hours))) {
            throw new DateTimeValidationException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .reason("Incorrectly made request.")
                            .message("Field: eventDate. Error: event date must be at least 2 hours in the future. " +
                                    "Value: " + eventDate)
                            .build()
            );
        }
    }

    private void setDefaultRequestModeration(EventCreationDto eventCreationDto) {
        Boolean requestModeration = eventCreationDto.getRequestModeration();
        eventCreationDto.setRequestModeration(requestModeration != null ? requestModeration : true);
    }

    private void setDefaultParticipantLimit(EventCreationDto eventCreationDto) {
        Integer participantLimit = eventCreationDto.getParticipantLimit();
        eventCreationDto.setParticipantLimit(participantLimit != null ? participantLimit : 0);
    }

    private void setDefaultPaid(EventCreationDto eventCreationDto) {
        Boolean paid = eventCreationDto.getPaid();
        eventCreationDto.setPaid(paid != null ? paid : false);
    }
}
