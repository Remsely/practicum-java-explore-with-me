package ru.practicum.explorewithme.mainsvc.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.event.entity.Location;
import ru.practicum.explorewithme.mainsvc.event.entity.LocationPrimaryKey;
import ru.practicum.explorewithme.mainsvc.event.repository.LocationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    public boolean isLocationExists(Location location) {
        return locationRepository.existsById(LocationPrimaryKey.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build()
        );
    }

    @Override
    public Location saveLocation(Location location) {
        Location savedLocation = locationRepository.save(location);
        log.info("Location has been saved : {}", savedLocation);
        return savedLocation;
    }
}
