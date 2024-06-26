package ru.practicum.explorewithme.statsvc.service.stat.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.statsvc.common.dto.HitDto;
import ru.practicum.explorewithme.statsvc.common.dto.StatDto;
import ru.practicum.explorewithme.statsvc.service.stat.entity.Stat;

@Component
public class StatMapper {
    public Stat hitToStat(HitDto dto) {
        return Stat.builder()
                .app(dto.getApp())
                .ip(dto.getIp())
                .uri(dto.getUri())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public HitDto statToHitDto(Stat stat) {
        return HitDto.builder()
                .app(stat.getApp())
                .ip(stat.getIp())
                .timestamp(stat.getTimestamp())
                .uri(stat.getUri())
                .build();
    }

    public StatDto statToStatDto(Stat stat) {
        return StatDto.builder()
                .hits(stat.getHits())
                .app(stat.getApp())
                .uri(stat.getUri())
                .build();
    }
}
