package ru.practicum.explorewithme.mainsvc.event.service;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.category.service.CategoryService;
import ru.practicum.explorewithme.mainsvc.common.requests.LocationRadiusRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.TimeRangeRequest;
import ru.practicum.explorewithme.mainsvc.common.stat.client.StatClientService;
import ru.practicum.explorewithme.mainsvc.common.utils.pageable.PageableUtility;
import ru.practicum.explorewithme.mainsvc.event.dto.creation.EventCreationDto;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventFullDto;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventShortDto;
import ru.practicum.explorewithme.mainsvc.event.dto.requests.EventsAdminRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.requests.EventsPublicRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.update.*;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.entity.EventState;
import ru.practicum.explorewithme.mainsvc.event.mapper.EventMapper;
import ru.practicum.explorewithme.mainsvc.event.repository.EventRepository;
import ru.practicum.explorewithme.mainsvc.event.util.EventQueryDslUtility;
import ru.practicum.explorewithme.mainsvc.event_request.dto.EventRequestDto;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequest;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;
import ru.practicum.explorewithme.mainsvc.event_request.mapper.EventRequestMapper;
import ru.practicum.explorewithme.mainsvc.event_request.repository.EventRequestRepository;
import ru.practicum.explorewithme.mainsvc.exception.AccessRightsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.exception.NotPublicException;
import ru.practicum.explorewithme.mainsvc.exception.RequestsAlreadyCompletedException;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationDto;
import ru.practicum.explorewithme.mainsvc.location.entity.Location;
import ru.practicum.explorewithme.mainsvc.location.mapper.LocationMapper;
import ru.practicum.explorewithme.mainsvc.location.service.LocationService;
import ru.practicum.explorewithme.mainsvc.user.entity.User;
import ru.practicum.explorewithme.mainsvc.user.service.UserService;
import ru.practicum.explorewithme.statsvc.common.dto.StatDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final EventQueryDslUtility eventQueryDslUtility;

    private final EventRequestMapper requestMapper;
    private final EventRequestRepository requestRepository;

    private final LocationService locationService;
    private final LocationMapper locationMapper;

    private final CategoryService categoryService;

    private final UserService userService;

    private final PageableUtility pageableUtility;

    private final StatClientService statClientService;

    @Transactional
    @Override
    public EventFullDto addEvent(EventCreationDto dto, long userId) {
        User user = userService.findUserById(userId);
        Category category = categoryService.findCategoryById(dto.getCategory());
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
        Event event = findEventById(eventId);
        User user = userService.findUserById(userId);

        if (!userIsEventInitiator(user, event)) {
            throw new AccessRightsException("User with id = " + user.getId() +
                    " is not initiator of the event with id = " + event.getId() + ".");
        }
        if (!eventIsCanceledOrPending(event)) {
            throw new AccessRightsException("Event " + event.getId() + " is not in " + EventState.PENDING +
                    " or " + EventState.CANCELED + " state.");
        }

        updateEventProperties(event, dto);
        updateEventStateByUser(event, dto);

        return updateEventInDB(event);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(long eventId, EventAdminUpdateDto dto) {
        Event event = findEventById(eventId);
        updateEventStateByAdmin(event, dto);
        updateEventProperties(event, dto);
        return updateEventInDB(event);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getUserEventById(long eventId, long userId) {
        User user = userService.findUserById(userId);
        Event event = findEventById(eventId);

        if (!userIsEventInitiator(user, event)) {
            throw new AccessRightsException("User with id = " + user.getId() +
                    " is not initiator of the event with id = " + event.getId() + ".");
        }

        int confirmedRequests = requestRepository.countByEventAndStatus(event, EventRequestStatus.CONFIRMED);
        long views = event.getState().equals(EventState.PUBLISHED) ? getEventViews(event) : 0;

        EventFullDto eventDto = eventMapper.toFullDto(event, confirmedRequests, views);
        log.info("User event has been found : {}", eventDto);
        return eventDto;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getPublicEventById(long eventId) {
        Event event = findEventById(eventId);

        if (!eventIsPublished(event)) {
            throw new NotPublicException("Event " + event.getId() + " is not public.");
        }

        int confirmedRequests = requestRepository.countByEventAndStatus(event, EventRequestStatus.CONFIRMED);
        long views = getEventViews(event);

        EventFullDto eventDto = eventMapper.toFullDto(event, confirmedRequests, views);
        log.info("Event has been found : {}", eventDto);
        return eventDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsByUser(long userId, PaginationRequest paginationRequest) {
        userService.findUserById(userId);

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
        JPAQuery<Event> query = eventQueryDslUtility.getQuery();

        eventQueryDslUtility.addUsersFilter(query, eventsAdminRequests.getUsers());
        eventQueryDslUtility.addStatesFilter(query, eventsAdminRequests.getStates());
        eventQueryDslUtility.addCategoriesFilter(query, eventsAdminRequests.getCategories());
        eventQueryDslUtility.addTimeRangeFilter(query, timeRangeRequest);
        eventQueryDslUtility.addPaginationFilter(query, paginationRequest);

        List<Event> events = eventQueryDslUtility.getQueryResultWithFetchJoins(query);
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
                                               EventsPublicRequest eventsPublicRequest,
                                               LocationRadiusRequest locationRequest) {
        JPAQuery<Event> query = eventQueryDslUtility.getQuery();

        eventQueryDslUtility.addPublishedFilter(query);

        eventQueryDslUtility.addTextSearchFilter(query, eventsPublicRequest.getText());
        eventQueryDslUtility.addCategoriesFilter(query, eventsPublicRequest.getCategories());
        eventQueryDslUtility.addPaidFilter(query, eventsPublicRequest.getPaid());

        if (timeRangeRequest.getRangeStart() == null && timeRangeRequest.getRangeEnd() == null) {
            eventQueryDslUtility.addFutureDateFilter(query);
        } else {
            eventQueryDslUtility.addTimeRangeFilter(query, timeRangeRequest);
        }

        eventQueryDslUtility.addOnlyAvailableFilter(query, eventsPublicRequest.getOnlyAvailable());

        boolean isSortByViews = false;
        EventsPublicRequest.SortType sort = eventsPublicRequest.getSort();
        if (sort != null) {
            switch (sort) {
                case VIEWS:
                    isSortByViews = true;
                    break;
                case EVENT_DATE:
                    eventQueryDslUtility.addOrderByEventDate(query);
                    break;
            }
        }

        eventQueryDslUtility.addLocationFilter(query, locationRequest);
        eventQueryDslUtility.addPaginationFilter(query, paginationRequest);

        List<Event> events = eventQueryDslUtility.getQueryResultWithFetchJoins(query);
        log.info("List size : {}", events.size());
        List<EventRequest> confirmedRequests = requestRepository
                .findByEventInAndStatus(events, EventRequestStatus.CONFIRMED);
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
        User user = userService.findUserById(userId);
        Event event = findEventById(eventId);

        if (!userIsEventInitiator(user, event)) {
            throw new AccessRightsException("User with id = " + user.getId() +
                    " is not initiator of the event with id = " + event.getId() + ".");
        }

        List<EventRequest> requests = requestRepository.findByEvent(event);
        List<EventRequestDto> dtos = requestMapper.toDtoList(requests);
        log.info("Requests have been found. List size : {}", dtos.size());
        return dtos;
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResultDto updateEventRequestsByUser(
            long eventId, EventRequestStatusUpdateRequestDto request, long userId) {
        User user = userService.findUserById(userId);
        Event event = findEventById(eventId);

        if (!userIsEventInitiator(user, event)) {
            throw new AccessRightsException("User with id = " + user.getId() +
                    " is not initiator of the event with id = " + event.getId() + ".");
        }

        List<EventRequest> requestsToUpdate = requestRepository.findByEventAndIdIn(event, request.getRequestIds());
        requestsToUpdate.forEach(r -> {
            if (!r.getStatus().equals(EventRequestStatus.PENDING)) {
                throw new AccessRightsException("Event request with id = " + r.getId() + " is not pending.");
            }
            if (eventParticipantLimitIsCompleted(event)) {
                throw new RequestsAlreadyCompletedException("Event " + event.getId() + " is full.");
            }
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

    @Transactional(readOnly = true)
    @Override
    public Event findEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Event with id = " + id + " not found.")
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Event> findEventsByIdIn(Set<Long> ids) {
        Set<Event> events = eventRepository.findByIdIn(ids);
        if (events.size() != ids.size()) {
            throw new EntityNotFoundException("No all events with ids = " + ids + " found.");
        }
        return events;
    }

    @Override
    public boolean userIsEventInitiator(User user, Event event) {
        return user.getId().equals(event.getInitiator().getId());
    }

    @Override
    public boolean eventIsPublished(Event event) {
        return event.getState().equals(EventState.PUBLISHED);
    }

    @Override
    public boolean eventParticipantLimitIsCompleted(Event event) {
        if (event.getParticipantLimit() == 0) {
            return false;
        }
        int confirmedRequestsCont = requestRepository.countByEventAndStatus(event, EventRequestStatus.CONFIRMED);
        return event.getParticipantLimit() <= confirmedRequestsCont;
    }

    private boolean eventIsPending(Event event) {
        return event.getState().equals(EventState.PENDING);
    }

    private boolean eventIsCanceledOrPending(Event event) {
        return event.getState() == EventState.CANCELED || event.getState() == EventState.PENDING;
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
                if (eventIsPublished(updating)) {
                    throw new AccessRightsException("Event " + updating.getId() + " is not public.");
                }
                updating.setState(EventState.CANCELED);
                break;
            case PUBLISH_EVENT:
                if (!eventIsPending(updating)) {
                    throw new AccessRightsException("Event " + updating.getId() + " is not in " +
                            EventState.PENDING + " state.");
                }
                updating.setState(EventState.PUBLISHED);
                updating.setPublishedOn(LocalDateTime.now());
                break;
        }
    }

    private void updateCategory(Event updating, EventUpdateDto updater) {
        Long categoryId = updater.getCategory();
        if (categoryId != null && !categoryId.equals(updating.getCategory().getId())) {
            Category category = categoryService.findCategoryById(categoryId);
            updating.setCategory(category);
        }
    }

    private void updateLocation(Event updating, EventUpdateDto updater) {
        LocationDto locationDto = updater.getLocation();
        if (locationDto != null) {
            Location newLocation = locationMapper.toEntity(updater.getLocation());
            updating.setLocation(locationService.putLocation(newLocation));
        }
    }

    private long getEventViews(Event event) {
        List<StatDto> stats = statClientService.getEventsStats(
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
        return statClientService.getEventsStats(
                eventIds,
                LocalDateTime.of(1970, 1, 1, 0, 0),
                LocalDateTime.now());
    }
}
