package ru.practicum.explorewithme.mainsvc.event.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import ru.practicum.explorewithme.mainsvc.event.entity.EventState;

import java.util.List;

@Validated
@Getter
@Setter
public class EventsAdminRequests {
    private List<Long> users;
    private List<EventState> states;
    private List<Long> categories;
}
