package ru.practicum.explorewithme.mainsvc.location.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.common.requests.LocationRadiusRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.pageable.PageableUtility;
import ru.practicum.explorewithme.mainsvc.event.repository.EventRepository;
import ru.practicum.explorewithme.mainsvc.exception.AccessRightsException;
import ru.practicum.explorewithme.mainsvc.exception.AlreadyExistsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationDto;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationUpdateDto;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationsAdminRequest;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationsPublicRequest;
import ru.practicum.explorewithme.mainsvc.location.entity.Location;
import ru.practicum.explorewithme.mainsvc.location.mapper.LocationMapper;
import ru.practicum.explorewithme.mainsvc.location.repository.LocationRepository;
import ru.practicum.explorewithme.mainsvc.location.util.LocationQueryDslUtility;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final LocationQueryDslUtility queryDslUtility;
    private final EventRepository eventRepository;
    private final PageableUtility pageableUtility;

    @Transactional
    @Override
    public LocationDto addLocation(LocationDto locationDto) {
        if (locationRepository.existsByLatAndLonAndRadiusAndNameAndVerified(
                locationDto.getLat(), locationDto.getLon(), locationDto.getRadius(), locationDto.getName(), true)
        ) {
            throw new AlreadyExistsException("Verified location " + locationDto + " already exists.");
        }
        Location location = locationMapper.toEntity(locationDto);

        location.setVerified(true);
        Location savedLocation = locationRepository.save(location);

        LocationDto result = locationMapper.toDto(savedLocation);
        log.info("Location has been created : {}.", result);
        return result;
    }

    @Transactional
    @Override
    public LocationDto updateLocation(long id, LocationUpdateDto locationDto) {
        Location location = findLocationById(id);

        updateLocationProperties(location, locationDto);
        Location savedLocation = locationRepository.save(location);

        LocationDto result = locationMapper.toDto(savedLocation);
        log.info("Location has been updated : {}.", result);
        return result;
    }

    @Transactional
    @Override
    public void deleteLocations(List<Long> ids) {
        if (!locationRepository.existsAllByIdIn(ids)) {
            throw new EntityNotFoundException("Not all locations by ids " + ids + " not found.");
        }
        if (eventRepository.existsByLocationIdIn(ids)) {
            throw new AccessRightsException("There are events at locations with ids " + ids + ".");
        }
        locationRepository.deleteAllByIdIn(ids);
        log.info("Locations {} has been deleted.", ids);
    }

    @Transactional(readOnly = true)
    @Override
    public LocationDto getLocation(long id) {
        Location location = findLocationById(id);
        LocationDto result = locationMapper.toDto(location);
        log.info("Location has been found : {}.", result);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<LocationDto> getLocationsByAdmin(LocationsAdminRequest locationsRequest,
                                                 PaginationRequest paginationRequest) {
        Pageable pageable = pageableUtility.toPageable(paginationRequest);

        List<Location> locations;
        if (Boolean.TRUE.equals(locationsRequest.getVerified())) {
            locations = locationRepository.findByVerified(true, pageable);
        } else {
            locations = locationRepository.findByVerified(false, pageable);
        }

        List<LocationDto> dtos = locationMapper.toDtoList(locations);
        log.info("Locations has been found. List size : {}.", dtos.size());
        return dtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<LocationDto> getPublicLocations(LocationsPublicRequest locationsRequest,
                                                LocationRadiusRequest locationRadiusRequest,
                                                PaginationRequest paginationRequest) {
        var query = queryDslUtility.getQuery();

        queryDslUtility.addVerifiedFilter(query, true);
        queryDslUtility.addTextSearchFilter(query, locationsRequest.getText());
        queryDslUtility.addLocationFilter(query, locationRadiusRequest);

        queryDslUtility.addPaginationFilter(query, paginationRequest);

        List<Location> locations = queryDslUtility.getQueryResultWithFetchJoins(query);
        List<LocationDto> dtos = locationMapper.toDtoList(locations);
        log.info("Public locations has been found. List size : {}.", dtos.size());
        return dtos;
    }

    @Transactional
    @Override
    public Location putLocation(Location location) {
        if (location.getId() == null) {
            Optional<Location> existsLocation = locationRepository.getByLatAndLonAndRadiusAndNameAndVerified(
                    location.getLat(), location.getLon(), location.getRadius(), location.getName(), true
            );
            if (existsLocation.isPresent()) {
                log.info("Location already exists : {}", existsLocation.get());
                return existsLocation.get();
            }
            location.setVerified(false);
            Location savedLocation = locationRepository.save(location);
            log.info("Location has been saved : {}", savedLocation);
            return savedLocation;
        }
        Location existsLocation = findLocationById(location.getId());
        log.info("Location already exists : {}", existsLocation);
        return existsLocation;
    }

    @Override
    public Location findLocationById(long id) {
        return locationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Location with id " + id + " not found.")
        );
    }

    private void updateLocationProperties(Location updating, LocationUpdateDto updater) {
        if (locationRepository.existsByLatAndLonAndRadiusAndNameAndVerified(
                updater.getLat(), updater.getLon(), updater.getRadius(), updater.getName(), true)
        ) {
            throw new AlreadyExistsException("Location " + updater + " already exists.");
        }
        String name = updater.getName();
        if (name != null) {
            updating.setName(name);
        }
        Double lat = updater.getLat();
        if (lat != null) {
            updating.setLat(lat);
        }
        Double lon = updater.getLon();
        if (lon != null) {
            updating.setLon(lon);
        }
        Double radius = updater.getRadius();
        if (radius != null) {
            updating.setRadius(radius);
        }
        Boolean verified = updater.getVerified();
        if (verified != null) {
            updating.setVerified(verified);
        }
    }
}
