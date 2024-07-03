package ru.practicum.explorewithme.mainsvc.event.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@Builder
public class EventUpdateDto {
    @ToString.Exclude
    @Length(min = 20, max = 2000, message = "Event annotation should be between 20 and 2000 characters.")
    private String annotation;

    private Long category;

    @ToString.Exclude
    @Length(min = 20, max = 7000, message = "Event description should be between 20 and 7000 characters.")
    private String description;

    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero(message = "Event participantLimit should be greater than 0 or equal to 0.")
    private Integer participantLimit;

    private Boolean requestModeration;

    private UserStateAction stateAction;

    @Length(min = 3, max = 120, message = "Event title should be between 3 and 120 characters.")
    private String title;
}
