package ru.practicum.explorewithme.mainsvc.event.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.explorewithme.mainsvc.category.dto.CategoryDto;
import ru.practicum.explorewithme.mainsvc.event.entity.EventState;
import ru.practicum.explorewithme.mainsvc.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class EventDto {
    @ToString.Exclude
    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    private LocalDateTime createdOn;

    @ToString.Exclude
    private String description;

    private LocalDateTime eventDate;

    private Long id;

    private UserDto initiator;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    private String title;

    private Long views;
}
