package ru.practicum.explorewithme.mainsvc.event.dto.info;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.explorewithme.mainsvc.category.dto.CategoryDto;
import ru.practicum.explorewithme.mainsvc.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class EventShortDto {
    @ToString.Exclude
    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    private LocalDateTime eventDate;

    private Long id;

    private UserDto initiator;

    private Boolean paid;

    private String title;

    private Long views;
}
