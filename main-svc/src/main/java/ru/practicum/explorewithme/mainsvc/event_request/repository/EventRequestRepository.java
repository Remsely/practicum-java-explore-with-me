package ru.practicum.explorewithme.mainsvc.event_request.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequest;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

import java.util.List;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    boolean existsByRequesterAndEvent(User user, Event event);

    Integer countByEventAndStatus(Event event, EventRequestStatus status);

    @EntityGraph(attributePaths = {"event", "requester"})
    List<EventRequest> findByEventInAndStatus(List<Event> events, EventRequestStatus status);

    @EntityGraph(attributePaths = {"event", "requester"})
    List<EventRequest> findByEventAndIdIn(Event event, List<Long> requestIds);

    @EntityGraph(attributePaths = {"event", "requester"})
    List<EventRequest> findByEventAndStatus(Event event, EventRequestStatus status);

    @EntityGraph(attributePaths = {"event", "requester"})
    List<EventRequest> findByEvent(Event event);

    @EntityGraph(attributePaths = {"event", "requester"})
    List<EventRequest> findByRequester(User user);
}
