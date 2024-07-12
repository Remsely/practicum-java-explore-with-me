package ru.practicum.explorewithme.mainsvc.common.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import ru.practicum.explorewithme.mainsvc.exception.LocationValidationException;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Validated
@Getter
@Setter
public class LocationRadiusRequest {
    @Max(value = 90, message = "Latitude should be less than 90.")
    @Min(value = -90, message = "Latitude should be more than -90.")
    private Double lat;

    @Max(value = 180, message = "Longitude should be less than 180.")
    @Min(value = -180, message = "Longitude should be more than -180.")
    private Double lon;

    @Positive
    @Max(value = 100, message = "Radius should be less than 100 km.")
    private Double radius;

    public void validate() {
        if (lat == null && lon == null && radius == null || lat != null && lon != null) {
            return;
        }
        throw new LocationValidationException("Latitude, longitude and radius should be specified together " +
                "or only latitude and longitude.");
    }
}
