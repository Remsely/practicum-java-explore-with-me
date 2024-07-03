package ru.practicum.explorewithme.mainsvc.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.mainsvc.event.entity.Location;
import ru.practicum.explorewithme.mainsvc.event.entity.LocationPrimaryKey;

@Repository
public interface LocationRepository extends JpaRepository<Location, LocationPrimaryKey> {
    @Query(" select count(e) > 0 " +
            "from Event e " +
            "where e.location = :location and e.id <> :eventId")
    boolean existsInEventsExclude(Location location, long eventId);
}
