package ru.practicum.explorewithme.mainsvc.event_request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.util.EventGuardService;
import ru.practicum.explorewithme.mainsvc.event_request.dto.EventRequestDto;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequest;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;
import ru.practicum.explorewithme.mainsvc.event_request.mapper.EventRequestMapper;
import ru.practicum.explorewithme.mainsvc.event_request.repository.EventRequestRepository;
import ru.practicum.explorewithme.mainsvc.event_request.util.EventRequestGuardService;
import ru.practicum.explorewithme.mainsvc.user.entity.User;
import ru.practicum.explorewithme.mainsvc.user.util.UserGuardService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRequestServiceImpl implements EventRequestService {
    private final EventRequestGuardService requestExceptionThrower;
    private final EventRequestRepository requestRepository;
    private final EventRequestMapper requestMapper;

    private final EventGuardService eventExceptionThrower;

    private final UserGuardService userExceptionThrower;

    @Transactional
    @Override
    public EventRequestDto addRequest(long eventId, long userId) {
        User user = userExceptionThrower.findById(userId);
        Event event = eventExceptionThrower.findById(eventId);

        requestExceptionThrower.checkExistenceByUserAndEvent(user, event);
        eventExceptionThrower.checkUserIsNotInitiator(user, event);
        eventExceptionThrower.checkStatusIsPublished(event);
        eventExceptionThrower.checkParticipantLimit(event);

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
        User user = userExceptionThrower.findById(userId);
        EventRequest request = requestExceptionThrower.findById(requestId);

        requestExceptionThrower.checkUserIsRequester(user, request);
        requestExceptionThrower.checkStatusIsPending(request);

        request.setStatus(EventRequestStatus.CANCELED);
        EventRequest savedRequest = requestRepository.save(request);

        EventRequestDto dto = requestMapper.toDto(savedRequest);
        log.info("Request has been canceled : {}", dto);
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventRequestDto> getRequestsByUserId(long userId) {
        User user = userExceptionThrower.findById(userId);
        List<EventRequest> requests = requestRepository.findByRequester(user);
        List<EventRequestDto> dtos = requestMapper.toDtoList(requests);
        log.info("Requests has been found. List size : {}", dtos);
        return dtos;
    }
}
