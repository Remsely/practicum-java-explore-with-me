package ru.practicum.explorewithme.mainsvc.event.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.EntityByIdExistenceGuard;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.entity.EventState;
import ru.practicum.explorewithme.mainsvc.event.repository.EventRepository;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;
import ru.practicum.explorewithme.mainsvc.event_request.repository.EventRequestRepository;
import ru.practicum.explorewithme.mainsvc.exception.AccessRightsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.exception.NotPublicException;
import ru.practicum.explorewithme.mainsvc.exception.RequestsAlreadyCompletedException;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventGuardService implements EntityByIdExistenceGuard<Event, Long> {
    private final EventRepository eventRepository;
    private final EventRequestRepository requestRepository;

    @Override
    public Event findById(Long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Event with id = " + id + " not found.")
        );
    }

    @Override
    public void checkExistenceById(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event with id = " + id + " not found.");
        }
    }

    public Set<Event> findByIdIn(Set<Long> ids) {
        Set<Event> events = eventRepository.findByIdIn(ids);
        if (events.size() != ids.size()) {
            throw new EntityNotFoundException("No all events with ids = " + ids + " found.");
        }
        return events;
    }

    public void checkUserIsInitiator(User user, Event event) {
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new AccessRightsException("User with id = " + user.getId() +
                    " is not initiator of the event with id = " + event.getId() + ".");
        }
    }

    public void checkUserIsNotInitiator(User user, Event event) {
        if (user.getId().equals(event.getInitiator().getId())) {
            throw new AccessRightsException("User with id = " + user.getId() +
                    " is initiator of the event with id = " + event.getId() + ".");
        }
    }

    public void checkStatusIsPending(Event event) {
        if (event.getState() != EventState.PENDING) {
            throw new AccessRightsException("Event " + event.getId() + " is not in " + EventState.PENDING + " state.");
        }
    }

    public void checkStatusIsPublished(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new AccessRightsException("Event " + event.getId() + " is not in " +
                    EventState.PUBLISHED + " state.");
        }
    }

    public void checkStatusIsNotPublished(Event event) {
        if (event.getState() == EventState.PUBLISHED) {
            throw new AccessRightsException("Event " + event.getId() + " is already published.");
        }
    }

    public void checkStatusIsCanceledOrPending(Event event) {
        if (event.getState() != EventState.CANCELED && event.getState() != EventState.PENDING) {
            throw new AccessRightsException("Event " + event.getId() + " is not in " + EventState.PENDING +
                    " or " + EventState.CANCELED + " state.");
        }
    }

    public void checkEventIsPublic(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotPublicException("Event " + event.getId() + " is not public.");
        }
    }

    public void checkParticipantLimit(Event event) {
        if (event.getParticipantLimit() == 0) {
            return;
        }
        int confirmedRequestsCont = requestRepository.countByEventAndStatus(event, EventRequestStatus.CONFIRMED);
        if (!(event.getParticipantLimit() > confirmedRequestsCont)) {
            throw new RequestsAlreadyCompletedException("Event " + event.getId() + " is full.");
        }
    }
}
