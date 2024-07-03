package ru.practicum.explorewithme.mainsvc.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.entity.Location;
import ru.practicum.explorewithme.mainsvc.event.entity.LocationPrimaryKey;
import ru.practicum.explorewithme.mainsvc.event.repository.LocationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    @Override
    public boolean isLocationExists(Location location) {
        return locationRepository.existsById(LocationPrimaryKey.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build()
        );
    }

    @Transactional
    @Override
    public Location putLocation(Location location) {
        if (!isLocationExists(location)) {
            Location savedLocation = locationRepository.save(location);
            log.info("Location has been saved : {}", savedLocation);
            return savedLocation;
        }
        return location;
    }

    @Transactional
    @Override
    public boolean deleteEventLocation(Location location, Event event) {
        if (locationRepository.existsInEventsExclude(location, event.getId())) {
            return false;
        }
        locationRepository.deleteById(LocationPrimaryKey.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build()
        );
        return true;
    }
}
