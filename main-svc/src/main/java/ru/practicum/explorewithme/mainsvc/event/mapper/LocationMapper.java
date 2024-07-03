package ru.practicum.explorewithme.mainsvc.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.event.dto.LocationDto;
import ru.practicum.explorewithme.mainsvc.event.entity.Location;

@Component
public class LocationMapper {
    public LocationDto toDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public Location toEntity(LocationDto locationDto) {
        return locationDto == null ? null : Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }
}
