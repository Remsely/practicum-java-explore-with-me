package ru.practicum.explorewithme.mainsvc.event.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.category.entity.QCategory;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.TimeRangeRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.CategoryExceptionThrower;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.EventExceptionThrower;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.RequestExceptionThrower;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.UserExceptionThrower;
import ru.practicum.explorewithme.mainsvc.common.utils.pageable.PageableUtility;
import ru.practicum.explorewithme.mainsvc.event.dto.*;
import ru.practicum.explorewithme.mainsvc.event.entity.*;
import ru.practicum.explorewithme.mainsvc.event.entity.QEvent;
import ru.practicum.explorewithme.mainsvc.event.entity.QLocation;
import ru.practicum.explorewithme.mainsvc.event.mapper.EventMapper;
import ru.practicum.explorewithme.mainsvc.event.mapper.LocationMapper;
import ru.practicum.explorewithme.mainsvc.event.repository.EventRepository;
import ru.practicum.explorewithme.mainsvc.request.dto.RequestDto;
import ru.practicum.explorewithme.mainsvc.request.entity.Request;
import ru.practicum.explorewithme.mainsvc.request.entity.RequestStatus;
import ru.practicum.explorewithme.mainsvc.request.mapper.RequestMapper;
import ru.practicum.explorewithme.mainsvc.request.repository.RequestRepository;
import ru.practicum.explorewithme.mainsvc.user.entity.QUser;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final EventExceptionThrower eventExceptionThrower;
    private final LocationService locationService;
    private final LocationMapper locationMapper;
    private final CategoryExceptionThrower categoryExceptionThrower;
    private final UserExceptionThrower userExceptionThrower;
    private final RequestRepository requestRepository;
    private final RequestExceptionThrower requestExceptionThrower;
    private final RequestMapper requestMapper;
    private final PageableUtility pageableUtility;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public EventDto addEvent(EventCreationDto dto, long userId) {
        User user = userExceptionThrower.findById(userId);
        Category category = categoryExceptionThrower.findById(dto.getCategory());
        Event event = eventMapper.toEntity(dto);

        event.setInitiator(user);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());

        if (event.getParticipantLimit() == 0) {
            event.setRequestModeration(false);
        }

        Location location = event.getLocation();
        event.setLocation(locationService.putLocation(location));

        Event savedEvent = eventRepository.save(event);
        EventDto eventDto = eventMapper.toDto(savedEvent, 0, 0);
        log.info("Event has been saved : {}", eventDto);
        return eventDto;
    }

    @Transactional
    @Override
    public EventDto updateEventByUser(long eventId, EventUserUpdateDto dto, long userId) {
        Event event = eventExceptionThrower.findById(eventId);
        User user = userExceptionThrower.findById(userId);

        eventExceptionThrower.checkUserIsInitiator(user, event);
        eventExceptionThrower.checkStatusIsCanceledOrPending(event);

        updateEventProperties(event, dto);
        updateEventStateByUser(event, dto);

        return updateEventInDB(event);
    }

    @Transactional
    @Override
    public EventDto updateEventByAdmin(long eventId, EventAdminUpdateDto dto) {
        Event event = eventExceptionThrower.findById(eventId);
        updateEventStateByAdmin(event, dto);
        updateEventProperties(event, dto);
        return updateEventInDB(event);
    }

    @Transactional(readOnly = true)
    @Override
    public EventDto getEventById(long eventId, long userId) {
        User user = userExceptionThrower.findById(userId);
        Event event = eventExceptionThrower.findById(eventId);

        eventExceptionThrower.checkUserIsInitiator(user, event);

        int confirmedRequests = requestRepository.countByEventAndStatus(event, RequestStatus.CONFIRMED);
        EventDto eventDto = eventMapper.toDto(event, confirmedRequests, 0);
        log.info("Event has been found : {}", eventDto);
        return eventDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsByUser(long userId, PaginationRequest paginationRequest) {
        userExceptionThrower.checkExistenceById(userId);

        List<Event> events = eventRepository.findByInitiatorId(userId, pageableUtility.toPageable(paginationRequest));
        List<Request> requests = requestRepository.findByEventInAndStatus(events, RequestStatus.CONFIRMED);

        List<EventShortDto> dtos = eventMapper.toShortDtoList(events, requests, Map.of());
        log.info("Events have been found : {}", dtos);
        return dtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> getEventsByAdmin(PaginationRequest paginationRequest,
                                           TimeRangeRequest timeRangeRequest,
                                           EventsAdminRequests eventsAdminRequests) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QEvent event = QEvent.event;
        QCategory category = QCategory.category;
        QUser user = QUser.user;
        QLocation location = QLocation.location;

        var query = queryFactory.selectFrom(event)
                .leftJoin(event.category, category).fetchJoin()
                .leftJoin(event.initiator, user).fetchJoin()
                .leftJoin(event.location, location).fetchJoin();

        List<Long> userIds = eventsAdminRequests.getUsers();
        if (userIds != null && !userIds.isEmpty()) {
            query.where(event.initiator.id.in(userIds));
        }

        List<EventState> states = eventsAdminRequests.getStates();
        if (states != null && !states.isEmpty()) {
            query.where(event.state.in(states));
        }

        List<Long> categoriesIds = eventsAdminRequests.getCategories();
        if (categoriesIds != null && !categoriesIds.isEmpty()) {
            query.where(event.category.id.in(categoriesIds));
        }

        LocalDateTime rangeStart = timeRangeRequest.getRangeStart();
        if (rangeStart != null) {
            query.where(event.eventDate.goe(rangeStart));
        }

        LocalDateTime rangeEnd = timeRangeRequest.getRangeEnd();
        if (rangeEnd != null) {
            query.where(event.eventDate.loe(rangeEnd));
        }

        Integer from = paginationRequest.getFrom();
        Integer size = paginationRequest.getSize();
        query.offset(from == null ? 0 : from).limit(size == null ? 10 : size);

        List<Event> events = query.fetch();
        List<Request> confirmedRequests = requestRepository.findByEventInAndStatus(events, RequestStatus.CONFIRMED);

        List<EventDto> dtos = eventMapper.toDtoList(events, confirmedRequests, Map.of());
        log.info("Events have been found. List size : {}", dtos);
        return dtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getEventRequestsByUser(long eventId, long userId) {
        User user = userExceptionThrower.findById(userId);
        Event event = eventExceptionThrower.findById(eventId);

        eventExceptionThrower.checkUserIsInitiator(user, event);

        List<Request> requests = requestRepository.findByEvent(event);
        List<RequestDto> dtos = requestMapper.toDtoList(requests);
        log.info("Requests have been found. List size : {}", dtos.size());
        return dtos;
    }

    @Transactional
    @Override
    public RequestStatusUpdateResultDto updateEventRequestsByUser(
            long eventId, RequestStatusUpdateRequestDto request, long userId) {
        User user = userExceptionThrower.findById(userId);
        Event event = eventExceptionThrower.findById(eventId);

        eventExceptionThrower.checkUserIsInitiator(user, event);

        List<Request> requestsToUpdate = requestRepository.findByEventAndIdIn(event, request.getRequestIds());
        requestsToUpdate.forEach(r -> {
            requestExceptionThrower.checkStatusIsPending(r);
            eventExceptionThrower.checkParticipantLimit(event);
            r.setStatus(request.getStatus());
        });
        requestRepository.saveAll(requestsToUpdate);

        List<Request> confirmedRequests = requestRepository.findByEventAndStatus(event, RequestStatus.CONFIRMED);
        List<Request> rejectedRequests = requestRepository.findByEventAndStatus(event, RequestStatus.REJECTED);

        RequestStatusUpdateResultDto result = RequestStatusUpdateResultDto.builder()
                .confirmedRequests(requestMapper.toDtoList(confirmedRequests))
                .rejectedRequests(requestMapper.toDtoList(rejectedRequests))
                .build();
        log.info("Requests statuses has been updated. Confirmed requests size: {}, rejected requests size: {}",
                result.getConfirmedRequests().size(), result.getRejectedRequests().size());
        return result;
    }

    private EventDto updateEventInDB(Event event) {
        Event savedEvent = eventRepository.save(event);
        EventDto savedEventDto = eventMapper.toDto(savedEvent, 0, 0);
        log.info("Event has been updated : {}", savedEventDto);
        return savedEventDto;
    }

    private void updateEventProperties(Event updating, EventUpdateDto updater) {
        String annotation = updater.getAnnotation();
        if (annotation != null) {
            updating.setAnnotation(annotation);
        }
        String description = updater.getDescription();
        if (description != null) {
            updating.setDescription(description);
        }
        LocalDateTime eventDate = updater.getEventDate();
        if (eventDate != null) {
            updating.setEventDate(eventDate);
        }
        Boolean paid = updater.getPaid();
        if (paid != null) {
            updating.setPaid(paid);
        }
        Boolean requestModeration = updater.getRequestModeration();
        if (requestModeration != null) {
            updating.setRequestModeration(requestModeration);
        }
        Integer participantLimit = updater.getParticipantLimit();
        if (participantLimit != null) {
            updating.setParticipantLimit(participantLimit);
            if (participantLimit == 0) {
                updating.setRequestModeration(false);
            }
        }
        String title = updater.getTitle();
        if (title != null) {
            updating.setTitle(title);
        }

        updateCategory(updating, updater);
        updateLocation(updating, updater);
    }

    private void updateEventStateByUser(Event updating, EventUserUpdateDto updater) {
        if (updater.getStateAction() == null) {
            return;
        }
        switch (updater.getStateAction()) {
            case CANCEL_REVIEW:
                updating.setState(EventState.CANCELED);
                break;
            case SEND_TO_REVIEW:
                updating.setState(EventState.PENDING);
                break;
        }
    }

    private void updateEventStateByAdmin(Event updating, EventAdminUpdateDto updater) {
        if (updater.getStateAction() == null) {
            return;
        }
        switch (updater.getStateAction()) {
            case REJECT_EVENT:
                eventExceptionThrower.checkStatusIsNotPublished(updating);
                updating.setState(EventState.CANCELED);
                break;
            case PUBLISH_EVENT:
                eventExceptionThrower.checkStatusIsPending(updating);
                updating.setState(EventState.PUBLISHED);
                updating.setPublishedOn(LocalDateTime.now());
                break;
        }
    }

    private void updateCategory(Event updating, EventUpdateDto updater) {
        Long categoryId = updater.getCategory();
        if (categoryId != null && !categoryId.equals(updating.getCategory().getId())) {
            Category category = categoryExceptionThrower.findById(categoryId);
            updating.setCategory(category);
        }
    }

    private void updateLocation(Event updating, EventUpdateDto updater) {
        Location newLocation = locationMapper.toEntity(updater.getLocation());
        if (newLocation != null) {
            Location prevLocation = updating.getLocation();
            updating.setLocation(locationService.putLocation(newLocation));
            locationService.deleteEventLocation(prevLocation, updating);
        }
    }
}
