package ru.practicum.explorewithme.statsvc.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatDto {
    private String app;
    private String uri;
    private int hits;
}
