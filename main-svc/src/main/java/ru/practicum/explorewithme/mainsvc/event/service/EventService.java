package ru.practicum.explorewithme.mainsvc.event.service;

import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.TimeRangeRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.creation.EventCreationDto;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventFullDto;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventShortDto;
import ru.practicum.explorewithme.mainsvc.event.dto.requests.EventsAdminRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.requests.EventsPublicRequest;
import ru.practicum.explorewithme.mainsvc.event.dto.update.EventAdminUpdateDto;
import ru.practicum.explorewithme.mainsvc.event.dto.update.EventRequestStatusUpdateRequestDto;
import ru.practicum.explorewithme.mainsvc.event.dto.update.EventRequestStatusUpdateResultDto;
import ru.practicum.explorewithme.mainsvc.event.dto.update.EventUserUpdateDto;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event_request.dto.EventRequestDto;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

import java.util.List;
import java.util.Set;

public interface EventService {
    EventFullDto addEvent(EventCreationDto dto, long userId);

    EventFullDto updateEventByUser(long eventId, EventUserUpdateDto dto, long userId);

    EventFullDto updateEventByAdmin(long eventId, EventAdminUpdateDto dto);

    EventFullDto getUserEventById(long eventId, long userId);

    EventFullDto getPublicEventById(long eventId);

    List<EventShortDto> getEventsByUser(long userId, PaginationRequest paginationRequest);

    List<EventFullDto> getEventsByAdmin(PaginationRequest paginationRequest,
                                        TimeRangeRequest timeRangeRequest,
                                        EventsAdminRequest eventsAdminRequests);

    List<EventShortDto> getPublicEvents(PaginationRequest paginationRequest,
                                        TimeRangeRequest timeRangeRequest,
                                        EventsPublicRequest eventsPublicRequest);

    List<EventRequestDto> getEventRequestsByUser(long eventId, long userId);

    EventRequestStatusUpdateResultDto updateEventRequestsByUser(
            long eventId, EventRequestStatusUpdateRequestDto request, long userId);

    Event findEventById(Long id);

    Set<Event> findEventsByIdIn(Set<Long> ids);

    boolean eventParticipantLimitIsCompleted(Event event);

    boolean eventIsPublished(Event event);

    boolean userIsEventInitiator(User user, Event event);
}
