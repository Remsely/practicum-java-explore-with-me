package ru.practicum.explorewithme.mainsvc.event.dto.creation;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import ru.practicum.explorewithme.mainsvc.location.dto.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@Builder
public class EventCreationDto {
    @ToString.Exclude
    @NotBlank(message = "Event annotation should not be blank.")
    @Length(min = 20, max = 2000, message = "Event annotation should be between 20 and 2000 characters.")
    private String annotation;

    @NotNull(message = "Event category should not be undefined.")
    private Long category;

    @ToString.Exclude
    @NotBlank(message = "Event description should not be blank.")
    @Length(min = 20, max = 7000, message = "Event description should be between 20 and 7000 characters.")
    private String description;

    @NotNull(message = "Event event date should not be undefined.")
    private LocalDateTime eventDate;

    @NotNull(message = "Event location should not be undefined.")
    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero(message = "Event participant limit should be greater than 0 or equal to 0.")
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank(message = "Event title should not be blank.")
    @Length(min = 3, max = 120, message = "Event title should be between 3 and 120 characters.")
    private String title;
}
