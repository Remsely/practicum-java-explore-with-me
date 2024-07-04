package ru.practicum.explorewithme.mainsvc.location.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class LocationDto {
    @NotNull(message = "Latitude should not be undefined.")
    @Max(value = 90, message = "Latitude should be less than 90")
    @Min(value = -90, message = "Latitude should be more than -90")
    private Double lat;

    @NotNull(message = "Longitude should not be undefined.")
    @Max(value = 180, message = "Longitude should be less than 180")
    @Min(value = -180, message = "Longitude should be more than -180")
    private Double lon;
}
