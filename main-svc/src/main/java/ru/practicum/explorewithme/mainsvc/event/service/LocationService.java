package ru.practicum.explorewithme.mainsvc.event.service;

import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.entity.Location;

public interface LocationService {
    boolean isLocationExists(Location location);

    Location putLocation(Location location);

    boolean deleteEventLocation(Location location, Event event);
}
