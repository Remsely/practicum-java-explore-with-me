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

@Component
public class CompilationQueryDslUtility extends QueryDslUtility<Compilation, QCompilation> {
    @Override
    public JPAQuery<Compilation> getQueryWithFetchJoins(QCompilation compilation) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QEvent event = QEvent.event;
        QCategory category = QCategory.category;
        QUser user = QUser.user;
        QLocation location = QLocation.location;

        return queryFactory.selectFrom(compilation)
                .leftJoin(compilation.events, event).fetchJoin()
                .leftJoin(event.category, category).fetchJoin()
                .leftJoin(event.initiator, user).fetchJoin()
                .leftJoin(event.location, location).fetchJoin();
    }

    public void addPinnedFilter(JPAQuery<Compilation> query, QCompilation compilation, Boolean pinned) {
        if (pinned != null) {
            if (pinned) {
                query.where(compilation.pinned.isTrue());
            } else {
                query.where(compilation.pinned.isFalse());
            }
        }
    }
}
