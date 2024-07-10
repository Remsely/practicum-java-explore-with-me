package ru.practicum.explorewithme.mainsvc.event_request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.service.EventService;
import ru.practicum.explorewithme.mainsvc.event_request.dto.EventRequestDto;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequest;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;
import ru.practicum.explorewithme.mainsvc.event_request.mapper.EventRequestMapper;
import ru.practicum.explorewithme.mainsvc.event_request.repository.EventRequestRepository;
import ru.practicum.explorewithme.mainsvc.exception.AccessRightsException;
import ru.practicum.explorewithme.mainsvc.exception.AlreadyExistsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.exception.RequestsAlreadyCompletedException;
import ru.practicum.explorewithme.mainsvc.user.entity.User;
import ru.practicum.explorewithme.mainsvc.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRequestServiceImpl implements EventRequestService {
    private final EventRequestRepository requestRepository;
    private final EventRequestMapper requestMapper;

    private final EventService eventService;

    private final UserService userService;

    @Transactional
    @Override
    public EventRequestDto addRequest(long eventId, long userId) {
        User user = userService.findUserById(userId);
        Event event = eventService.findEventById(eventId);

        if (requestExistsByUserAndEvent(user, event)) {
            throw new AlreadyExistsException("Request already exists for event with id = " + event.getId() +
                    " from user with id = " + user.getId() + ".");
        }
        if (eventService.userIsEventInitiator(user, event)) {
            throw new AccessRightsException("User " + user.getId() + " is initiator of event " + event.getId());
        }
        if (!eventService.eventIsPublished(event)) {
            throw new AccessRightsException("Event " + event.getId() + " is not public.");
        }
        if (eventService.eventParticipantLimitIsCompleted(event)) {
            throw new RequestsAlreadyCompletedException("Event " + event.getId() + " is full.");
        }

        EventRequest request = EventRequest.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .status(event.getRequestModeration().equals(Boolean.TRUE)
                        ? event.getParticipantLimit() == 0 ? EventRequestStatus.CONFIRMED : EventRequestStatus.PENDING
                        : EventRequestStatus.CONFIRMED)
                .build();
        EventRequest savedRequest = requestRepository.save(request);

        EventRequestDto requestDto = requestMapper.toDto(savedRequest);
        log.info("Request has been saved : {}", requestDto);
        return requestDto;
    }

    @Transactional
    @Override
    public EventRequestDto cancelRequest(long requestId, long userId) {
        User user = userService.findUserById(userId);
        EventRequest request = findRequestById(requestId);

        if (!userIsRequester(user, request)) {
            throw new AccessRightsException("User with id = " + user.getId() +
                    " is not requester of event request with id = " + request.getId() + ".");
        }
        if (!requestIsPending(request)) {
            throw new AccessRightsException("Event request with id = " + request.getId() + " is not pending.");
        }

        request.setStatus(EventRequestStatus.CANCELED);
        EventRequest savedRequest = requestRepository.save(request);

        EventRequestDto dto = requestMapper.toDto(savedRequest);
        log.info("Request has been canceled : {}", dto);
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventRequestDto> getRequestsByUserId(long userId) {
        User user = userService.findUserById(userId);
        List<EventRequest> requests = requestRepository.findByRequester(user);
        List<EventRequestDto> dtos = requestMapper.toDtoList(requests);
        log.info("Requests has been found. List size : {}", dtos);
        return dtos;
    }

    @Transactional(readOnly = true)
    @Override
    public EventRequest findRequestById(Long id) {
        return requestRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Event request with id = " + id + " not found.")
        );
    }

    private boolean requestExistsByUserAndEvent(User user, Event event) {
        return requestRepository.existsByRequesterAndEvent(user, event);
    }

    private boolean userIsRequester(User user, EventRequest request) {
        return request.getRequester().getId().equals(user.getId());
    }

    private boolean requestIsPending(EventRequest request) {
        return request.getStatus().equals(EventRequestStatus.PENDING);
    }
}
