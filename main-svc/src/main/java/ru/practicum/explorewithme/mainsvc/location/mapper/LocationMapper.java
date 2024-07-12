package ru.practicum.explorewithme.mainsvc.location.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationDto;
import ru.practicum.explorewithme.mainsvc.location.entity.Location;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocationMapper {
    public Location toEntity(LocationDto locationDto) {
        return locationDto == null ? null : Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .radius(locationDto.getRadius())
                .name(locationDto.getName())
                .build();
    }

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

    public List<LocationDto> toDtoList(List<Location> locations) {
        return locations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
