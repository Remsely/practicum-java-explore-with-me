package ru.practicum.explorewithme.mainsvc.request.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.request.entity.Request;
import ru.practicum.explorewithme.mainsvc.request.entity.RequestStatus;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByRequesterAndEvent(User user, Event event);

    Integer countByEventAndStatus(Event event, RequestStatus status);

    @EntityGraph(attributePaths = {"event", "requester"})
    List<Request> findByEventInAndStatus(List<Event> events, RequestStatus status);
}
