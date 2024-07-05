package ru.practicum.explorewithme.mainsvc.event.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.ByIdExceptionThrower;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.entity.EventState;
import ru.practicum.explorewithme.mainsvc.event.repository.EventRepository;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;
import ru.practicum.explorewithme.mainsvc.event_request.repository.EventRequestRepository;
import ru.practicum.explorewithme.mainsvc.exception.AccessRightsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.exception.NotPublicException;
import ru.practicum.explorewithme.mainsvc.exception.RequestsAlreadyCompletedException;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventExceptionThrower implements ByIdExceptionThrower<Event, Long> {
    private final EventRepository eventRepository;
    private final EventRequestRepository requestRepository;

    @Override
    public Event findById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ErrorResponseDto.builder()
                        .status(HttpStatus.NOT_FOUND.toString())
                        .reason("Event not found.")
                        .message("Event with id = " + id + " not found.")
                        .timestamp(LocalDateTime.now())
                        .build()
        ));
    }

    @Override
    public void checkExistenceById(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.NOT_FOUND.toString())
                            .reason("Event not found.")
                            .message("Event with id = " + id + " not found.")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    public Set<Event> findByIdIn(Set<Long> ids) {
        Set<Event> events = eventRepository.findByIdIn(ids);
        if (events.size() != ids.size()) {
            throw new EntityNotFoundException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.NOT_FOUND.toString())
                            .reason("Event not found.")
                            .message("Not all events with ids = " + ids + " found.")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
        return events;
    }

    public void checkUserIsInitiator(User user, Event event) {
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new AccessRightsException(ErrorResponseDto.builder()
                    .status(HttpStatus.CONFLICT.toString())
                    .reason("Access rights error.")
                    .message("User " + user.getId() + " is not an initiator of event " + event.getId() + ".")
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }
    }

    public void checkUserIsNotInitiator(User user, Event event) {
        if (user.getId().equals(event.getInitiator().getId())) {
            throw new AccessRightsException(ErrorResponseDto.builder()
                    .status(HttpStatus.CONFLICT.toString())
                    .reason("Access rights error.")
                    .message("User with id = " + user.getId() + " is an initiator of the event with id = "
                            + event.getId() + ".")
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }
    }

    public void checkStatusIsPending(Event event) {
        if (event.getState() != EventState.PENDING) {
            throw new AccessRightsException(ErrorResponseDto.builder()
                    .status(HttpStatus.CONFLICT.toString())
                    .reason("Incorrect event status.")
                    .message("Event " + event.getId() + " is not in " + EventState.PENDING + " state.")
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }
    }

    public void checkStatusIsPublished(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new AccessRightsException(ErrorResponseDto.builder()
                    .status(HttpStatus.CONFLICT.toString())
                    .reason("Incorrect event status.")
                    .message("Event " + event.getId() + " is not in " + EventState.PUBLISHED + " state.")
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }
    }

    public void checkStatusIsNotPublished(Event event) {
        if (event.getState() == EventState.PUBLISHED) {
            throw new AccessRightsException(ErrorResponseDto.builder()
                    .status(HttpStatus.CONFLICT.toString())
                    .reason("Incorrect event status.")
                    .message("Event " + event.getId() + " is in " + EventState.PUBLISHED + " state.")
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }
    }

    public void checkStatusIsCanceledOrPending(Event event) {
        if (event.getState() != EventState.CANCELED && event.getState() != EventState.PENDING) {
            throw new AccessRightsException(ErrorResponseDto.builder()
                    .status(HttpStatus.CONFLICT.toString())
                    .reason("Incorrect event status.")
                    .message("Event " + event.getId() + " is not in "
                            + EventState.CANCELED + " or " + EventState.PENDING + " state.")
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }
    }

    public void checkEventIsPublic(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotPublicException(ErrorResponseDto.builder()
                    .status(HttpStatus.NOT_FOUND.toString())
                    .reason("Public event not found.")
                    .message("Public event with id = " + event.getId() + " not found.")
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }
    }

    public void checkParticipantLimit(Event event) {
        if (event.getParticipantLimit() == 0) {
            return;
        }
        int confirmedRequestsCont = requestRepository.countByEventAndStatus(event, EventRequestStatus.CONFIRMED);
        if (!(event.getParticipantLimit() > confirmedRequestsCont)) {
            throw new RequestsAlreadyCompletedException(ErrorResponseDto.builder()
                    .status(HttpStatus.CONFLICT.toString())
                    .reason("Requests already completed.")
                    .message("Requests for event with id = " + event.getId() + " already completed.")
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }
    }
}
