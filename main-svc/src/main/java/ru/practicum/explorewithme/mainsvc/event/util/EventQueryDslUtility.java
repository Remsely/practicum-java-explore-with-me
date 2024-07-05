package ru.practicum.explorewithme.mainsvc.event.util;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.category.entity.QCategory;
import ru.practicum.explorewithme.mainsvc.common.requests.TimeRangeRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.querydsl.QueryDslUtility;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.entity.EventState;
import ru.practicum.explorewithme.mainsvc.event.entity.QEvent;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;
import ru.practicum.explorewithme.mainsvc.event_request.entity.QEventRequest;
import ru.practicum.explorewithme.mainsvc.location.entity.QLocation;
import ru.practicum.explorewithme.mainsvc.user.entity.QUser;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventQueryDslUtility extends QueryDslUtility<Event, QEvent> {
    @Override
    public JPAQuery<Event> getQueryWithFetchJoins(QEvent event) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QCategory category = QCategory.category;
        QUser user = QUser.user;
        QLocation location = QLocation.location;

        return queryFactory.selectFrom(event)
                .leftJoin(event.category, category).fetchJoin()
                .leftJoin(event.initiator, user).fetchJoin()
                .leftJoin(event.location, location).fetchJoin();
    }

    public void addPublishedFilter(JPAQuery<Event> query, QEvent event) {
        query.where(event.state.eq(EventState.PUBLISHED));
    }

    public void addTimeRangeFilter(JPAQuery<Event> query, QEvent event, TimeRangeRequest timeRangeRequest) {
        LocalDateTime rangeStart = timeRangeRequest.getRangeStart();
        if (rangeStart != null) {
            query.where(event.eventDate.goe(rangeStart));
        }

        LocalDateTime rangeEnd = timeRangeRequest.getRangeEnd();
        if (rangeEnd != null) {
            query.where(event.eventDate.loe(rangeEnd));
        }
    }

    public void addCategoriesFilter(JPAQuery<Event> query, QEvent event, List<Long> categoriesIds) {
        if (categoriesIds != null && !categoriesIds.isEmpty()) {
            query.where(event.category.id.in(categoriesIds));
        }
    }

    public void addTextSearchFilter(JPAQuery<Event> query, QEvent event, String text) {
        if (text != null && !text.isBlank()) {
            String sqlText = "%" + text.toLowerCase() + "%";
            query.where(event.annotation.lower().like(sqlText)
                    .or(event.description.lower().like(sqlText))
            );
        }
    }

    public void addOnlyAvailableFilter(JPAQuery<Event> query, QEvent event, Boolean onlyAvailable) {
        if (Boolean.TRUE.equals(onlyAvailable)) {
            QEventRequest request = QEventRequest.eventRequest;
            query.leftJoin(request)
                    .on(request.event.eq(event).and(request.status.eq(EventRequestStatus.CONFIRMED)))
                    .groupBy(event.id)
                    .having(request.count().lt(event.participantLimit)
                            .or(event.participantLimit.eq(0)));
        }
    }

    public void addPaidFilter(JPAQuery<Event> query, QEvent event, Boolean paid) {
        if (paid != null) {
            BooleanExpression paidFilter = paid ? event.paid.isTrue() : event.paid.isFalse();
            query.where(paidFilter);
        }
    }

    public void addUsersFilter(JPAQuery<Event> query, QEvent event, List<Long> userIds) {
        if (userIds != null && !userIds.isEmpty()) {
            query.where(event.initiator.id.in(userIds));
        }
    }

    public void addStatesFilter(JPAQuery<Event> query, QEvent event, List<EventState> states) {
        if (states != null && !states.isEmpty()) {
            query.where(event.state.in(states));
        }
    }

    public void addFutureDateFilter(JPAQuery<Event> query, QEvent event) {
        query.where(event.eventDate.gt(LocalDateTime.now()));
    }

    public void addOrderByEventDate(JPAQuery<Event> query, QEvent event) {
        query.orderBy(event.eventDate.desc());
    }
}
