package ru.practicum.explorewithme.mainsvc.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.mainsvc.location.entity.Location;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> getByLatAndLonAndRadiusAndNameAndVerified(
            Double lat, Double lon, Double radius, String name, Boolean verified);
}
