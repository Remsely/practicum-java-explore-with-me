package ru.practicum.explorewithme.statsvc.service.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.statsvc.common.dto.HitDto;
import ru.practicum.explorewithme.statsvc.common.dto.StatDto;
import ru.practicum.explorewithme.statsvc.common.dto.StatsRequest;
import ru.practicum.explorewithme.statsvc.service.stat.entity.AppAndUriPrimaryKey;
import ru.practicum.explorewithme.statsvc.service.stat.entity.Stat;
import ru.practicum.explorewithme.statsvc.service.stat.mapper.StatMapper;
import ru.practicum.explorewithme.statsvc.service.stat.repository.StatRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;
    private final StatMapper statMapper;

    @Override
    public HitDto commitHit(HitDto hitDto) {
        Optional<Stat> statOptional = statRepository.findById(AppAndUriPrimaryKey.builder()
                .app(hitDto.getApp())
                .ip(hitDto.getIp())
                .uri(hitDto.getUri())
                .build()
        );

        Stat stat;
        if (statOptional.isPresent()) {
            stat = statOptional.get();
            stat.setHits(stat.getHits() + 1);
        } else {
            stat = statMapper.hitToStat(hitDto);
            stat.setHits(1);
        }
        Stat savedStat = statRepository.save(stat);
        return statMapper.statToHitDto(savedStat);
    }

    @Override
    public List<StatDto> getStats(StatsRequest request) {
        return List.of();
    }
}
