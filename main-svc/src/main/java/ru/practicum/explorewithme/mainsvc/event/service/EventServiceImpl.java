package ru.practicum.explorewithme.mainsvc.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.category.entity.QCategory;
import ru.practicum.explorewithme.mainsvc.category.util.CategoryExceptionThrower;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.TimeRangeRequest;
import ru.practicum.explorewithme.mainsvc.common.stat.client.StatClientHelper;
import ru.practicum.explorewithme.mainsvc.common.utils.pageable.PageableUtility;
import ru.practicum.explorewithme.mainsvc.event.dto.creation.EventCreationDto;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventFullDto;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventShortDto;
import ru.practicum.explorewithme.mainsvc.event.dto.requests.EventsAdminRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.requests.EventsPublicRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.update.*;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.entity.EventState;
import ru.practicum.explorewithme.mainsvc.event.entity.QEvent;
import ru.practicum.explorewithme.mainsvc.event.mapper.EventMapper;
import ru.practicum.explorewithme.mainsvc.event.repository.EventRepository;
import ru.practicum.explorewithme.mainsvc.event.util.EventExceptionThrower;
import ru.practicum.explorewithme.mainsvc.event_request.dto.EventRequestDto;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequest;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;
import ru.practicum.explorewithme.mainsvc.event_request.entity.QEventRequest;
import ru.practicum.explorewithme.mainsvc.event_request.mapper.EventRequestMapper;
import ru.practicum.explorewithme.mainsvc.event_request.repository.EventRequestRepository;
import ru.practicum.explorewithme.mainsvc.event_request.util.EventRequestExceptionThrower;
import ru.practicum.explorewithme.mainsvc.location.entity.Location;
import ru.practicum.explorewithme.mainsvc.location.entity.QLocation;
import ru.practicum.explorewithme.mainsvc.location.mapper.LocationMapper;
import ru.practicum.explorewithme.mainsvc.location.service.LocationService;
import ru.practicum.explorewithme.mainsvc.user.entity.QUser;
import ru.practicum.explorewithme.mainsvc.user.entity.User;
import ru.practicum.explorewithme.mainsvc.user.util.UserExceptionThrower;
import ru.practicum.explorewithme.statsvc.common.dto.StatDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final EventExceptionThrower eventExceptionThrower;

    private final EventRequestMapper requestMapper;
    private final EventRequestRepository requestRepository;
    private final EventRequestExceptionThrower requestExceptionThrower;

    private final LocationService locationService;
    private final LocationMapper locationMapper;

    private final CategoryExceptionThrower categoryExceptionThrower;

    private final UserExceptionThrower userExceptionThrower;

    private final PageableUtility pageableUtility;

    private final StatClientHelper statClientHelper;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public EventFullDto addEvent(EventCreationDto dto, long userId) {
        User user = userExceptionThrower.findById(userId);
        Category category = categoryExceptionThrower.findById(dto.getCategory());
        Event event = eventMapper.toEntity(dto);

        event.setInitiator(user);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());

        Location location = event.getLocation();
        event.setLocation(locationService.putLocation(location));

        Event savedEvent = eventRepository.save(event);
        EventFullDto eventDto = eventMapper.toFullDto(savedEvent);
        log.info("Event has been saved : {}", eventDto);
        return eventDto;
    }

    @Transactional
    @Override
    public EventFullDto updateEventByUser(long eventId, EventUserUpdateDto dto, long userId) {
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
    public EventFullDto updateEventByAdmin(long eventId, EventAdminUpdateDto dto) {
        Event event = eventExceptionThrower.findById(eventId);
        updateEventStateByAdmin(event, dto);
        updateEventProperties(event, dto);
        return updateEventInDB(event);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getUserEventById(long eventId, long userId) {
        User user = userExceptionThrower.findById(userId);
        Event event = eventExceptionThrower.findById(eventId);

        eventExceptionThrower.checkUserIsInitiator(user, event);

        int confirmedRequests = requestRepository.countByEventAndStatus(event, EventRequestStatus.CONFIRMED);
        long views = event.getState().equals(EventState.PUBLISHED) ? getEventViews(event) : 0;

        EventFullDto eventDto = eventMapper.toFullDto(event, confirmedRequests, views);
        log.info("User event has been found : {}", eventDto);
        return eventDto;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getPublicEventById(long eventId) {
        Event event = eventExceptionThrower.findById(eventId);
        eventExceptionThrower.checkEventIsPublic(event);

        int confirmedRequests = requestRepository.countByEventAndStatus(event, EventRequestStatus.CONFIRMED);
        long views = getEventViews(event);

        EventFullDto eventDto = eventMapper.toFullDto(event, confirmedRequests, views);
        log.info("Event has been found : {}", eventDto);
        return eventDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsByUser(long userId, PaginationRequest paginationRequest) {
        userExceptionThrower.checkExistenceById(userId);

        List<Event> events = eventRepository.findByInitiatorId(userId, pageableUtility.toPageable(paginationRequest));
        List<EventRequest> requests = requestRepository.findByEventInAndStatus(events, EventRequestStatus.CONFIRMED);
        List<StatDto> stats = getEventsStats(events);

        List<EventShortDto> dtos = eventMapper.toShortDtoList(events, requests, stats);
        log.info("Events have been found : {}", dtos);
        return dtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getEventsByAdmin(PaginationRequest paginationRequest,
                                               TimeRangeRequest timeRangeRequest,
                                               EventsAdminRequest eventsAdminRequests) {
        QEvent event = QEvent.event;
        JPAQuery<Event> query = getQueryWithFetchJoins(event);

        addUsersFilter(query, event, eventsAdminRequests.getUsers());
        addStatesFilter(query, event, eventsAdminRequests.getStates());
        addCategoriesFilter(query, event, eventsAdminRequests.getCategories());
        addTimeRangeFilter(query, event, timeRangeRequest);
        addPaginationFilter(query, paginationRequest);

        List<Event> events = query.fetch();
        List<EventRequest> confirmedRequests = requestRepository.findByEventInAndStatus(events, EventRequestStatus.CONFIRMED);
        List<StatDto> stats = getEventsStats(events);

        List<EventFullDto> dtos = eventMapper.toDtoList(events, confirmedRequests, stats);
        log.info("Events have been found. List size : {}", dtos);
        return dtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getPublicEvents(PaginationRequest paginationRequest,
                                               TimeRangeRequest timeRangeRequest,
                                               EventsPublicRequest eventsPublicRequest) {
        QEvent event = QEvent.event;
        JPAQuery<Event> query = getQueryWithFetchJoins(event);

        query.where(event.state.eq(EventState.PUBLISHED));

        addTextSearchFilter(query, event, eventsPublicRequest.getText());
        addCategoriesFilter(query, event, eventsPublicRequest.getCategories());
        addPaidFilter(query, event, eventsPublicRequest.getPaid());

        if (timeRangeRequest.getRangeStart() == null && timeRangeRequest.getRangeEnd() == null) {
            query.where(event.eventDate.gt(LocalDateTime.now()));
        } else {
            addTimeRangeFilter(query, event, timeRangeRequest);
        }

        addOnlyAvailableFilter(query, event, eventsPublicRequest.getOnlyAvailable());

        boolean isSortByViews = false;
        EventsPublicRequest.SortType sort = eventsPublicRequest.getSort();
        if (sort != null) {
            switch (sort) {
                case VIEWS:
                    isSortByViews = true;
                    break;
                case EVENT_DATE:
                    query.orderBy(event.eventDate.desc());
                    break;
            }
        }

        addPaginationFilter(query, paginationRequest);

        List<Event> events = query.fetch();
        List<EventRequest> confirmedRequests = requestRepository.findByEventInAndStatus(events, EventRequestStatus.CONFIRMED);
        List<StatDto> stats = getEventsStats(events);

        List<EventShortDto> dtos = eventMapper.toShortDtoList(events, confirmedRequests, stats);
        if (isSortByViews) {
            dtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }
        log.info("Public events have been found. List size : {}", dtos.size());
        return dtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventRequestDto> getEventRequestsByUser(long eventId, long userId) {
        User user = userExceptionThrower.findById(userId);
        Event event = eventExceptionThrower.findById(eventId);

        eventExceptionThrower.checkUserIsInitiator(user, event);

        List<EventRequest> requests = requestRepository.findByEvent(event);
        List<EventRequestDto> dtos = requestMapper.toDtoList(requests);
        log.info("Requests have been found. List size : {}", dtos.size());
        return dtos;
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResultDto updateEventRequestsByUser(
            long eventId, EventRequestStatusUpdateRequestDto request, long userId) {
        User user = userExceptionThrower.findById(userId);
        Event event = eventExceptionThrower.findById(eventId);

        eventExceptionThrower.checkUserIsInitiator(user, event);

        List<EventRequest> requestsToUpdate = requestRepository.findByEventAndIdIn(event, request.getRequestIds());
        requestsToUpdate.forEach(r -> {
            requestExceptionThrower.checkStatusIsPending(r);
            eventExceptionThrower.checkParticipantLimit(event);
            r.setStatus(request.getStatus());
        });
        requestRepository.saveAll(requestsToUpdate);

        List<EventRequest> confirmedRequests = requestRepository.findByEventAndStatus(event, EventRequestStatus.CONFIRMED);
        List<EventRequest> rejectedRequests = requestRepository.findByEventAndStatus(event, EventRequestStatus.REJECTED);

        EventRequestStatusUpdateResultDto result = EventRequestStatusUpdateResultDto.builder()
                .confirmedRequests(requestMapper.toDtoList(confirmedRequests))
                .rejectedRequests(requestMapper.toDtoList(rejectedRequests))
                .build();
        log.info("Requests statuses has been updated. Confirmed requests size: {}, rejected requests size: {}",
                result.getConfirmedRequests().size(), result.getRejectedRequests().size());
        return result;
    }

    private EventFullDto updateEventInDB(Event event) {
        Event savedEvent = eventRepository.save(event);
        EventFullDto savedEventDto = eventMapper.toFullDto(savedEvent);
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

    private JPAQuery<Event> getQueryWithFetchJoins(QEvent event) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QCategory category = QCategory.category;
        QUser user = QUser.user;
        QLocation location = QLocation.location;

        return queryFactory.selectFrom(event)
                .leftJoin(event.category, category).fetchJoin()
                .leftJoin(event.initiator, user).fetchJoin()
                .leftJoin(event.location, location).fetchJoin();
    }

    private void addTimeRangeFilter(JPAQuery<Event> query, QEvent event, TimeRangeRequest timeRangeRequest) {
        LocalDateTime rangeStart = timeRangeRequest.getRangeStart();
        if (rangeStart != null) {
            query.where(event.eventDate.goe(rangeStart));
        }

        LocalDateTime rangeEnd = timeRangeRequest.getRangeEnd();
        if (rangeEnd != null) {
            query.where(event.eventDate.loe(rangeEnd));
        }
    }

    private void addPaginationFilter(JPAQuery<?> query, PaginationRequest paginationRequest) {
        Integer from = paginationRequest.getFrom();
        Integer size = paginationRequest.getSize();
        query.offset(from == null ? 0 : from).limit(size == null ? 10 : size);
    }

    private void addCategoriesFilter(JPAQuery<Event> query, QEvent event, List<Long> categoriesIds) {
        if (categoriesIds != null && !categoriesIds.isEmpty()) {
            query.where(event.category.id.in(categoriesIds));
        }
    }

    private void addTextSearchFilter(JPAQuery<Event> query, QEvent event, String text) {
        if (text != null && !text.isBlank()) {
            String sqlText = "%" + text.toLowerCase() + "%";
            query.where(event.annotation.lower().like(sqlText)
                    .or(event.description.lower().like(sqlText))
            );
        }
    }

    private void addOnlyAvailableFilter(JPAQuery<Event> query, QEvent event, Boolean onlyAvailable) {
        if (Boolean.TRUE.equals(onlyAvailable)) {
            QEventRequest request = QEventRequest.eventRequest;
            query.leftJoin(request)
                    .on(request.event.eq(event).and(request.status.eq(EventRequestStatus.CONFIRMED)))
                    .groupBy(event.id)
                    .having(request.count().lt(event.participantLimit)
                            .or(event.participantLimit.eq(0)));
        }
    }

    private void addPaidFilter(JPAQuery<Event> query, QEvent event, Boolean paid) {
        if (paid != null) {
            BooleanExpression paidFilter = paid ? event.paid.isTrue() : event.paid.isFalse();
            query.where(paidFilter);
        }
    }

    private void addUsersFilter(JPAQuery<Event> query, QEvent event, List<Long> userIds) {
        if (userIds != null && !userIds.isEmpty()) {
            query.where(event.initiator.id.in(userIds));
        }
    }

    private void addStatesFilter(JPAQuery<Event> query, QEvent event, List<EventState> states) {
        if (states != null && !states.isEmpty()) {
            query.where(event.state.in(states));
        }
    }

    private long getEventViews(Event event) {
        List<StatDto> stats = statClientHelper.getStats(
                List.of(event.getId()),
                event.getPublishedOn(),
                LocalDateTime.now()
        );
        return !stats.isEmpty() ? stats.get(0).getHits() : 0;
    }

    private List<StatDto> getEventsStats(List<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        return statClientHelper.getStats(
                eventIds,
                LocalDateTime.of(1970, 1, 1, 0, 0),
                LocalDateTime.now());
    }
}
