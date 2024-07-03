package ru.practicum.explorewithme.mainsvc.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.EventExceptionThrower;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.RequestExceptionThrower;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.UserExceptionThrower;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.request.dto.RequestDto;
import ru.practicum.explorewithme.mainsvc.request.entity.Request;
import ru.practicum.explorewithme.mainsvc.request.entity.RequestStatus;
import ru.practicum.explorewithme.mainsvc.request.mapper.RequestMapper;
import ru.practicum.explorewithme.mainsvc.request.repository.RequestRepository;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestExceptionThrower requestExceptionThrower;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final EventExceptionThrower eventExceptionThrower;
    private final UserExceptionThrower userExceptionThrower;

    @Transactional
    @Override
    public RequestDto addRequest(long eventId, long userId) {
        User user = userExceptionThrower.findById(userId);
        Event event = eventExceptionThrower.findById(eventId);

        requestExceptionThrower.checkExistenceByUserAndEvent(user, event);
        eventExceptionThrower.checkUserIsNotInitiator(user, event);
        eventExceptionThrower.checkStatusIsPublished(event);
        eventExceptionThrower.checkParticipantLimit(event);

        Request request = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .status(event.getRequestModeration().equals(Boolean.TRUE)
                        ? RequestStatus.PENDING
                        : RequestStatus.CONFIRMED)
                .build();
        Request savedRequest = requestRepository.save(request);

        RequestDto requestDto = requestMapper.toDto(savedRequest);
        log.info("Request has been saved : {}", requestDto);
        return requestDto;
    }

    @Transactional
    @Override
    public RequestDto cancelRequest(long requestId, long userId) {
        User user = userExceptionThrower.findById(userId);
        Request request = requestExceptionThrower.findById(requestId);

        requestExceptionThrower.checkUserIsRequester(user, request);
        requestExceptionThrower.checkStatusIsPending(request);

        request.setStatus(RequestStatus.CANCELED);
        Request savedRequest = requestRepository.save(request);

        RequestDto dto = requestMapper.toDto(savedRequest);
        log.info("Request has been canceled : {}", dto);
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getRequestsByUserId(long userId) {
        User user = userExceptionThrower.findById(userId);
        List<Request> requests = requestRepository.findByRequester(user);
        List<RequestDto> dtos = requestMapper.toDtoList(requests);
        log.info("Requests has been found. List size : {}", dtos);
        return dtos;
    }
}
