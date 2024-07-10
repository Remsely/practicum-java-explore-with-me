package ru.practicum.explorewithme.mainsvc.location.service;

import ru.practicum.explorewithme.mainsvc.common.requests.LocationRadiusRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationDto;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationUpdateDto;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationsAdminRequest;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationsPublicRequest;
import ru.practicum.explorewithme.mainsvc.location.entity.Location;

import java.util.List;

public interface LocationService {
    LocationDto addLocation(LocationDto locationDto);

    LocationDto updateLocation(long id, LocationUpdateDto locationDto);

    void deleteLocations(List<Long> ids);

    LocationDto getLocation(long id);

    List<LocationDto> getLocationsByAdmin(LocationsAdminRequest locationsRequest, PaginationRequest paginationRequest);

    List<LocationDto> getPublicLocations(LocationsPublicRequest locationsRequest,
                                         LocationRadiusRequest locationRadiusRequest,
                                         PaginationRequest paginationRequest);

    Location putLocation(Location location);

    Location findLocationById(long id);
}
