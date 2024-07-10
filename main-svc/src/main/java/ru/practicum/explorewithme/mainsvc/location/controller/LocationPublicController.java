package ru.practicum.explorewithme.mainsvc.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.mainsvc.common.requests.LocationRadiusRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationDto;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationsPublicRequest;
import ru.practicum.explorewithme.mainsvc.location.service.LocationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationPublicController {
    private final LocationService locationService;

    @GetMapping
    public List<LocationDto> getLocations(@ModelAttribute @Validated LocationsPublicRequest locationsRequest,
                                          @ModelAttribute @Validated LocationRadiusRequest locationRadiusRequest,
                                          @ModelAttribute @Validated PaginationRequest paginationRequest) {
        locationRadiusRequest.validate();
        log.info("Get public locations (/locations?text={}&radius={}&lat={}&lon={}&from={}&size={} GET).",
                locationsRequest.getText(), locationRadiusRequest.getRadius(), locationRadiusRequest.getLat(),
                locationRadiusRequest.getLon(), paginationRequest.getFrom(), paginationRequest.getSize()
        );
        return locationService.getPublicLocations(locationsRequest, locationRadiusRequest, paginationRequest);
    }
}
