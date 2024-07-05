package ru.practicum.explorewithme.mainsvc.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;

import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @EntityGraph(attributePaths = {"category", "location", "initiator"})
    List<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "location", "initiator"})
    Set<Event> findByIdIn(Set<Long> ids);
}
