package ru.practicum.explorewithme.mainsvc.location.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.location.entity.Location;
import ru.practicum.explorewithme.mainsvc.location.repository.LocationRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Transactional
    @Override
    public Location putLocation(Location location) {
        if (location.getId() == null) {
            Optional<Location> existsLocation = locationRepository.getByLatAndLonAndRadiusAndNameAndVerified(
                    location.getLat(), location.getLon(), location.getRadius(), location.getName(), true
            );
            if (existsLocation.isPresent()) {
                log.info("Location already exists : {}", existsLocation.get());
                return existsLocation.get();
            }
            location.setVerified(false);
            Location savedLocation = locationRepository.save(location);
            log.info("Location has been saved : {}", savedLocation);
            return savedLocation;
        }
        Location existsLocation = findLocationById(location.getId());
        log.info("Location already exists : {}", existsLocation);
        return existsLocation;
    }

    @Override
    public Location findLocationById(long id) {
        return locationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Location with id " + id + " not found.")
        );
    }
}
