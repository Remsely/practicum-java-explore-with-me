package ru.practicum.explorewithme.mainsvc.compilation.util;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.category.entity.QCategory;
import ru.practicum.explorewithme.mainsvc.common.utils.querydsl.QueryDslUtility;
import ru.practicum.explorewithme.mainsvc.compilation.entity.Compilation;
import ru.practicum.explorewithme.mainsvc.compilation.entity.QCompilation;
import ru.practicum.explorewithme.mainsvc.event.entity.QEvent;
import ru.practicum.explorewithme.mainsvc.location.entity.QLocation;
import ru.practicum.explorewithme.mainsvc.user.entity.QUser;

import java.util.List;

@Component
public class CompilationQueryDslUtility extends QueryDslUtility<Compilation, QCompilation> {
    public CompilationQueryDslUtility() {
        super(QCompilation.compilation);
    }

    @Override
    public JPAQuery<Compilation> getQuery() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        return queryFactory.selectFrom(qEntity);
    }

    @Override
    public List<Compilation> getQueryResultWithFetchJoins(JPAQuery<Compilation> query) {
        QEvent event = QEvent.event;
        QCategory category = QCategory.category;
        QUser user = QUser.user;
        QLocation location = QLocation.location;

        return query
                .leftJoin(qEntity.events, event).fetchJoin()
                .leftJoin(event.category, category).fetchJoin()
                .leftJoin(event.initiator, user).fetchJoin()
                .leftJoin(event.location, location).fetchJoin()
                .fetch();
    }

    public void addPinnedFilter(JPAQuery<Compilation> query, Boolean pinned) {
        if (pinned != null) {
            if (pinned) {
                query.where(qEntity.pinned.isTrue());
            } else {
                query.where(qEntity.pinned.isFalse());
            }
        }
    }
}
