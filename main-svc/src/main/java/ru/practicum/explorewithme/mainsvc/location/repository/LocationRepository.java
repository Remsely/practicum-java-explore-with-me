package ru.practicum.explorewithme.mainsvc.location.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.mainsvc.location.entity.Location;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> getByLatAndLonAndRadiusAndNameAndVerified(
            Double lat, Double lon, Double radius, String name, Boolean verified);

    boolean existsByLatAndLonAndRadiusAndNameAndVerified(
            Double lat, Double lon, Double radius, String name, Boolean verified);

    boolean existsAllByIdIn(List<Long> ids);

    void deleteAllByIdIn(List<Long> ids);

    List<Location> findByVerified(Boolean verified, Pageable pageable);
}
