package ru.practicum.explorewithme.mainsvc.event.dto.update;

import ru.practicum.explorewithme.mainsvc.location.dto.LocationDto;

import java.time.LocalDateTime;

public interface EventUpdateDto {
    String getAnnotation();

    String getDescription();

    LocalDateTime getEventDate();

    Boolean getPaid();

    Integer getParticipantLimit();

    Boolean getRequestModeration();

    String getTitle();

    Long getCategory();

    LocationDto getLocation();
}
