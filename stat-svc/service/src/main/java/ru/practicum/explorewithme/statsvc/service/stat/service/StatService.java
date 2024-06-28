package ru.practicum.explorewithme.statsvc.service.stat.service;

import ru.practicum.explorewithme.statsvc.common.dto.HitDto;
import ru.practicum.explorewithme.statsvc.common.dto.StatDto;
import ru.practicum.explorewithme.statsvc.common.dto.StatsRequest;

import java.util.List;

public interface StatService {
    HitDto commitHit(HitDto hitDto);

    List<StatDto> getStats(StatsRequest request);
}
