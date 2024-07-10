package ru.practicum.explorewithme.mainsvc.location.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationDto;
import ru.practicum.explorewithme.mainsvc.location.entity.Location;

@Component
public class LocationMapper {
    public LocationDto toDto(Location location) {
        return LocationDto.builder()
                .id(location.getId())
                .lat(location.getLat())
                .lon(location.getLon())
                .radius(location.getRadius())
                .name(location.getName())
                .verified(location.getVerified())
                .build();
    }

    public Location toEntity(LocationDto locationDto) {
        return locationDto == null ? null : Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .radius(locationDto.getRadius())
                .name(locationDto.getName())
                .build();
    }
}
