package ru.practicum.explorewithme.mainsvc.location.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.EntityByIdExistenceGuard;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.location.entity.Location;
import ru.practicum.explorewithme.mainsvc.location.entity.LocationPrimaryKey;
import ru.practicum.explorewithme.mainsvc.location.repository.LocationRepository;

@Service
@RequiredArgsConstructor
public class LocationGuardService implements EntityByIdExistenceGuard<Location, LocationPrimaryKey> {
    private final LocationRepository locationRepository;

    @Override
    public Location findById(LocationPrimaryKey id) {
        return locationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Location with id = " + id + " not found.")
        );
    }

    @Override
    public void checkExistenceById(LocationPrimaryKey id) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException("Location with id = " + id + " not found.");
        }
    }
}
