package ru.practicum.explorewithme.mainsvc.event.service;

import ru.practicum.explorewithme.mainsvc.event.entity.Location;

public interface LocationService {
    boolean isLocationExists(Location location);

    Location saveLocation(Location location);
}
