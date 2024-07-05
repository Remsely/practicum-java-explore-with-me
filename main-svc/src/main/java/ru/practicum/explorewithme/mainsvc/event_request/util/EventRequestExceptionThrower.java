package ru.practicum.explorewithme.mainsvc.event_request.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.ByIdExceptionThrower;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequest;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;
import ru.practicum.explorewithme.mainsvc.event_request.repository.EventRequestRepository;
import ru.practicum.explorewithme.mainsvc.exception.AccessRightsException;
import ru.practicum.explorewithme.mainsvc.exception.AlreadyExistsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventRequestExceptionThrower implements ByIdExceptionThrower<EventRequest, Long> {
    private final EventRequestRepository requestRepository;

    @Override
    public EventRequest findById(Long id) {
        return requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ErrorResponseDto.builder()
                        .status(HttpStatus.NOT_FOUND.toString())
                        .reason("Request not found")
                        .message("Request with id = " + id + " not found")
                        .timestamp(LocalDateTime.now())
                        .build()
        ));
    }

    @Override
    public void checkExistenceById(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.NOT_FOUND.toString())
                            .reason("Request not found")
                            .message("Request with id = " + id + " not found")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    public void checkExistenceByUserAndEvent(User user, Event event) {
        if (requestRepository.existsByRequesterAndEvent(user, event)) {
            throw new AlreadyExistsException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.CONFLICT.toString())
                            .reason("Request already exists.")
                            .message("Request with user = " + user.getId()
                                    + " and event = " + event.getId() + " already exists.")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    public void checkUserIsRequester(User user, EventRequest request) {
        if (!request.getRequester().getId().equals(user.getId())) {
            throw new AccessRightsException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.CONFLICT.toString())
                            .reason("User is not the request initiator.")
                            .message("User with id = " + user.getId() + " is not the request with id = "
                                    + request.getId() + " initiator.")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    public void checkStatusIsPending(EventRequest request) {
        if (!request.getStatus().equals(EventRequestStatus.PENDING)) {
            throw new AccessRightsException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.CONFLICT.toString())
                            .reason("Incorrect event request status.")
                            .message("Request with id = " + request.getId() + " status is not " + EventRequestStatus.PENDING
                                    + ". Current status : " + request.getStatus() + ".")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }
}
