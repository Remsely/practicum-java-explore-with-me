package ru.practicum.explorewithme.mainsvc.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationDto;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationUpdateDto;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationsAdminRequest;
import ru.practicum.explorewithme.mainsvc.location.service.LocationService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
public class LocationAdminController {
    private final LocationService locationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto addLocation(@RequestBody @Valid LocationDto locationDto) {
        log.info("Create location (/admin/locations POST). Body : {}", locationDto);
        return locationService.addLocation(locationDto);
    }

    @PatchMapping("/{locationId}")
    public LocationDto updateLocation(@PathVariable long locationId,
                                      @RequestBody @Valid LocationUpdateDto locationDto) {
        log.info("Update location (/admin/locations/{} PATCH). Body : {}", locationId, locationDto);
        return locationService.updateLocation(locationId, locationDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocations(@RequestParam List<Long> ids) {
        log.info("Delete locations (/admin/locations?ids={} DELETE).", ids);
        locationService.deleteLocations(ids);
    }

    @GetMapping("/{locationId}")
    public LocationDto getLocation(@PathVariable long locationId) {
        log.info("Get location (/admin/locations/{} GET).", locationId);
        return locationService.getLocation(locationId);
    }

    @GetMapping
    public List<LocationDto> getLocations(@ModelAttribute @Validated LocationsAdminRequest locationsRequest,
                                          @ModelAttribute @Validated PaginationRequest paginationRequest) {
        log.info("Get locations (/admin/locations?verified={}&from={}&size={} GET).",
                locationsRequest.getVerified(), paginationRequest.getFrom(), paginationRequest.getSize()
        );
        return locationService.getLocationsByAdmin(locationsRequest, paginationRequest);
    }
}
