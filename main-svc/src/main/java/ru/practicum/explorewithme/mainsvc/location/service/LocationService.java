package ru.practicum.explorewithme.mainsvc.location.service;

import ru.practicum.explorewithme.mainsvc.location.entity.Location;

public interface LocationService {
    Location putLocation(Location location);

    Location findLocationById(long id);
}
