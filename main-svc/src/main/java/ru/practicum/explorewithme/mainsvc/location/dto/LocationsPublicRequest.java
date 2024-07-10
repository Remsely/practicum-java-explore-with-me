package ru.practicum.explorewithme.mainsvc.location.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@Setter
public class LocationsPublicRequest {
    private String text;
}
