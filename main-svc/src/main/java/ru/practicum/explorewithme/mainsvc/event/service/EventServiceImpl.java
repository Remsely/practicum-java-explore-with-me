package ru.practicum.explorewithme.mainsvc.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.pageable.PageableUtility;
import ru.practicum.explorewithme.mainsvc.common.utils.repositories.CategoryRepositoryHelper;
import ru.practicum.explorewithme.mainsvc.common.utils.repositories.EventRepositoryHelper;
import ru.practicum.explorewithme.mainsvc.common.utils.repositories.UserRepositoryHelper;
import ru.practicum.explorewithme.mainsvc.event.dto.*;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.entity.Location;
import ru.practicum.explorewithme.mainsvc.event.mapper.EventMapper;
import ru.practicum.explorewithme.mainsvc.event.repository.EventRepository;
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
    private final CategoryRepositoryHelper categoryRepositoryHelper;
    private final UserRepositoryHelper userRepositoryHelper;
    private final PageableUtility pageableUtility;

    @Override
    public EventDto addEvent(EventCreationDto dto, long userId) {
        User user = userRepositoryHelper.findById(userId);
        Event event = eventMapper.toEntity(dto);
        event.setInitiator(user);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());

        Location location = event.getLocation();
        if (!locationService.isLocationExists(location)) {
            event.setLocation(locationService.saveLocation(location));
        }

        Event savedEvent = eventRepository.save(event);
        EventDto eventDto = eventMapper.toDto(savedEvent, 0, 0);
        log.info("Event has been saved : {}", eventDto);
        return eventDto;
    }

    @Override
    public EventDto updateEvent(long eventId, EventUpdateUserRequest dto, long userId) {
        return null;
    }

    @Override
    public EventDto getEventById(long eventId, long userId) {
        return null;
    }

    @Override
    public List<EventShortDto> getEventsByUser(long userId, PaginationRequest paginationRequest) {
        return List.of();
    }
}
