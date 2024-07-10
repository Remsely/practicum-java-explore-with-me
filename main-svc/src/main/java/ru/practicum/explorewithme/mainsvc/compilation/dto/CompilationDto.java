package ru.practicum.explorewithme.mainsvc.compilation.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventShortDto;

import java.util.Set;

@Data
@Builder
public class CompilationDto {
    private Set<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}
