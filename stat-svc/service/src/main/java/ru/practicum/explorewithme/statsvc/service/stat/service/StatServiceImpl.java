package ru.practicum.explorewithme.statsvc.service.stat.service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.statsvc.common.dto.HitDto;
import ru.practicum.explorewithme.statsvc.common.dto.StatDto;
import ru.practicum.explorewithme.statsvc.common.dto.StatsRequest;
import ru.practicum.explorewithme.statsvc.service.stat.entity.AppAndUriPrimaryKey;
import ru.practicum.explorewithme.statsvc.service.stat.entity.QStat;
import ru.practicum.explorewithme.statsvc.service.stat.entity.Stat;
import ru.practicum.explorewithme.statsvc.service.stat.mapper.StatMapper;
import ru.practicum.explorewithme.statsvc.service.stat.repository.StatRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;
    private final StatMapper statMapper;

    @PersistenceContext
    private EntityManager em;

    @Transactional
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
            stat.setHits(1L);
        }
        Stat savedStat = statRepository.save(stat);
        HitDto savedHit = statMapper.statToHitDto(savedStat);

        log.info("Hit has been saved : {}", savedHit);
        return savedHit;
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatDto> getStats(StatsRequest request) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QStat stat = QStat.stat;

        NumberExpression<Long> hitsSum = Boolean.TRUE.equals(request.getUnique())
                ? stat.ip.countDistinct()
                : stat.hits.sum();

        var query = queryFactory
                .select(Projections.constructor(
                        StatDto.class,
                        stat.app,
                        stat.uri,
                        hitsSum
                ))
                .from(stat)
                .where(stat.timestamp.between(request.getStart(), request.getEnd()))
                .groupBy(stat.app, stat.uri)
                .orderBy(hitsSum.desc());

        List<String> uris = request.getUris();
        if (uris != null && !uris.isEmpty()) {
            query.where(stat.uri.in(uris));
        }

        List<StatDto> statDtos = query.fetch();
        log.info("Stats received. List size :{}", statDtos.size());
        return statDtos;
    }
}
