package ru.practicum.explorewithme.mainsvc.event_request.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.EntityByIdExistenceGuard;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequest;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;
import ru.practicum.explorewithme.mainsvc.event_request.repository.EventRequestRepository;
import ru.practicum.explorewithme.mainsvc.exception.AccessRightsException;
import ru.practicum.explorewithme.mainsvc.exception.AlreadyExistsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

@Service
@RequiredArgsConstructor
public class EventRequestGuardService implements EntityByIdExistenceGuard<EventRequest, Long> {
    private final EventRequestRepository requestRepository;

    @Override
    public EventRequest findById(Long id) {
        return requestRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Event request with id = " + id + " not found.")
        );
    }

    @Override
    public void checkExistenceById(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new EntityNotFoundException("Event request with id = " + id + " not found.");
        }
    }

    public void checkExistenceByUserAndEvent(User user, Event event) {
        if (requestRepository.existsByRequesterAndEvent(user, event)) {
            throw new AlreadyExistsException("Request already exists for event with id = " + event.getId() +
                    " from user with id = " + user.getId() + ".");
        }
    }

    public void checkUserIsRequester(User user, EventRequest request) {
        if (!request.getRequester().getId().equals(user.getId())) {
            throw new AccessRightsException("User with id = " + user.getId() +
                    " is not requester of event request with id = " + request.getId() + ".");
        }
    }

    public void checkStatusIsPending(EventRequest request) {
        if (!request.getStatus().equals(EventRequestStatus.PENDING)) {
            throw new AccessRightsException("Event request with id = " + request.getId() + " is not pending.");
        }
    }
}
