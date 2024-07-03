package ru.practicum.explorewithme.mainsvc.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
