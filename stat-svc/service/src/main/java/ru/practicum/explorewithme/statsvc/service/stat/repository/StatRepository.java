package ru.practicum.explorewithme.statsvc.service.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.statsvc.service.stat.entity.AppAndUriPrimaryKey;
import ru.practicum.explorewithme.statsvc.service.stat.entity.Stat;

@Repository
public interface StatRepository extends JpaRepository<Stat, AppAndUriPrimaryKey>, QuerydslPredicateExecutor<Stat> {
}
