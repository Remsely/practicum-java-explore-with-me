package ru.practicum.explorewithme.mainsvc.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.pageable.PageableUtility;
import ru.practicum.explorewithme.mainsvc.common.utils.repositories.CategoryExceptionThrower;
import ru.practicum.explorewithme.mainsvc.common.utils.repositories.EventExceptionThrower;
import ru.practicum.explorewithme.mainsvc.common.utils.repositories.UserExceptionThrower;
import ru.practicum.explorewithme.mainsvc.event.dto.*;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.entity.EventState;
import ru.practicum.explorewithme.mainsvc.event.entity.Location;
import ru.practicum.explorewithme.mainsvc.event.mapper.EventMapper;
import ru.practicum.explorewithme.mainsvc.event.mapper.LocationMapper;
import ru.practicum.explorewithme.mainsvc.event.repository.EventRepository;
import ru.practicum.explorewithme.mainsvc.request.entity.Request;
import ru.practicum.explorewithme.mainsvc.request.entity.RequestStatus;
import ru.practicum.explorewithme.mainsvc.request.repository.RequestRepository;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

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
    private final PageableUtility pageableUtility;

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
        Integer participantLimit = updater.getParticipantLimit();
        if (participantLimit != null) {
            updating.setParticipantLimit(participantLimit);
        }
        Boolean requestModeration = updater.getRequestModeration();
        if (requestModeration != null) {
            updating.setRequestModeration(requestModeration);
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
