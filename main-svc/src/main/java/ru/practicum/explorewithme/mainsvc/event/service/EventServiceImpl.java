package ru.practicum.explorewithme.mainsvc.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.pageable.PageableUtility;
import ru.practicum.explorewithme.mainsvc.common.utils.repositories.CategoryRepositoryHelper;
import ru.practicum.explorewithme.mainsvc.common.utils.repositories.EventRepositoryHelper;
import ru.practicum.explorewithme.mainsvc.common.utils.repositories.UserRepositoryHelper;
import ru.practicum.explorewithme.mainsvc.event.dto.*;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.entity.Location;
import ru.practicum.explorewithme.mainsvc.event.mapper.EventMapper;
import ru.practicum.explorewithme.mainsvc.event.mapper.LocationMapper;
import ru.practicum.explorewithme.mainsvc.event.repository.EventRepository;
import ru.practicum.explorewithme.mainsvc.exception.AccessRightsException;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final EventRepositoryHelper eventRepositoryHelper;
    private final LocationService locationService;
    private final LocationMapper locationMapper;
    private final CategoryRepositoryHelper categoryRepositoryHelper;
    private final UserRepositoryHelper userRepositoryHelper;
    private final PageableUtility pageableUtility;

    @Transactional
    @Override
    public EventDto addEvent(EventCreationDto dto, long userId) {
        User user = userRepositoryHelper.findById(userId);
        Category category = categoryRepositoryHelper.findById(dto.getCategory());
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
    public EventDto updateEvent(long eventId, EventUpdateDto dto, long userId) {
        Event event = eventRepositoryHelper.findById(eventId);
        User user = userRepositoryHelper.findById(userId);

        checkInitiatorPermission(user, event);
        checkEventStatus(event);

        updateEventProperties(event, dto);
        Event savedEvent = eventRepository.save(event);

        EventDto savedEventDto = eventMapper.toDto(savedEvent, 0, 0);
        log.info("Event has been updated : {}", savedEventDto);
        return savedEventDto;
    }

    @Override
    public EventDto getEventById(long eventId, long userId) {
        return null;
    }

    @Override
    public List<EventShortDto> getEventsByUser(long userId, PaginationRequest paginationRequest) {
        return List.of();
    }

    private void checkInitiatorPermission(User user, Event event) {
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new AccessRightsException(ErrorResponseDto.builder()
                    .status(HttpStatus.FORBIDDEN.toString())
                    .reason("Access rights error.")
                    .message("User " + user.getId() + " is not an initiator of event " + event.getId() + ".")
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }
    }

    private void checkEventStatus(Event event) {
        if (event.getState() != EventState.CANCELED && event.getState() != EventState.PENDING) {
            throw new AccessRightsException(ErrorResponseDto.builder()
                    .status(HttpStatus.FORBIDDEN.toString())
                    .reason("Incorrect event status.")
                    .message("Event " + event.getId() + " is not in "
                            + EventState.CANCELED + " or " + EventState.PENDING + " state.")
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        }
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
        updateEventState(updating, updater);
    }

    private void updateEventState(Event updating, EventUpdateDto updater) {
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

    private void updateCategory(Event updating, EventUpdateDto updater) {
        Long categoryId = updater.getCategory();
        if (categoryId != null && !categoryId.equals(updating.getCategory().getId())) {
            Category category = categoryRepositoryHelper.findById(categoryId);
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
