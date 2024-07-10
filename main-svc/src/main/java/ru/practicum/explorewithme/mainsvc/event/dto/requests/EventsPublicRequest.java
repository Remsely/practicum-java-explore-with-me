package ru.practicum.explorewithme.mainsvc.event.dto.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Getter
@Setter
public class EventsPublicRequest {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private Boolean onlyAvailable;
    private SortType sort;

    public enum SortType {
        EVENT_DATE, VIEWS
    }
}
