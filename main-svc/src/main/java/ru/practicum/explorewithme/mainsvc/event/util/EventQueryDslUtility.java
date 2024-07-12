package ru.practicum.explorewithme.mainsvc.event.util;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.category.entity.QCategory;
import ru.practicum.explorewithme.mainsvc.common.requests.LocationRadiusRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.TimeRangeRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.querydsl.QueryDslUtility;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.entity.EventState;
import ru.practicum.explorewithme.mainsvc.event.entity.QEvent;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequestStatus;
import ru.practicum.explorewithme.mainsvc.event_request.entity.QEventRequest;
import ru.practicum.explorewithme.mainsvc.location.entity.QLocation;
import ru.practicum.explorewithme.mainsvc.location.util.GeoUtils;
import ru.practicum.explorewithme.mainsvc.user.entity.QUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventQueryDslUtility extends QueryDslUtility<Event, QEvent> {
    private static final double DEFAULT_LOCATION_RADIUS = 3; // default value for location radius if it's not provided
    private static final int DEFAULT_PAGINATION_FROM = 0; // default value for pagination from if it's not provided
    private static final int DEFAULT_PAGINATION_SIZE = 10; // default value for pagination size if it's not provided

    private final GeoUtils geoUtils;

    @Autowired
    public EventQueryDslUtility(GeoUtils geoUtils) {
        super(QEvent.event);
        this.geoUtils = geoUtils;
    }

    @Override
    public JPAQuery<Event> getQuery() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        return queryFactory.selectFrom(qEntity);
    }

    @Override
    public List<Event> getQueryResultWithFetchJoins(JPAQuery<Event> query) {
        QCategory category = QCategory.category;
        QUser user = QUser.user;
        QLocation location = QLocation.location;

        return query
                .leftJoin(qEntity.category, category).fetchJoin()
                .leftJoin(qEntity.initiator, user).fetchJoin()
                .leftJoin(qEntity.location, location).fetchJoin()
                .fetch();
    }

    public List<Event> getQueryResultWithLocationAndPaginationFilters(JPAQuery<Event> query,
                                                                      LocationRadiusRequest locationRadiusRequest,
                                                                      PaginationRequest paginationRequest) {
        List<Event> events = getQueryResultWithFetchJoins(query);
        events = filterLocationWithRadius(events, locationRadiusRequest);
        events = applyPagination(events, paginationRequest);
        return events;
    }

    public void addTextSearchFilter(JPAQuery<Event> query, String text) {
        if (text != null && !text.isBlank()) {
            String sqlText = "%" + text.toLowerCase() + "%";
            query.where(qEntity.annotation.lower().like(sqlText)
                    .or(qEntity.description.lower().like(sqlText))
            );
        }
    }

    public void addPublishedFilter(JPAQuery<Event> query) {
        query.where(qEntity.state.eq(EventState.PUBLISHED));
    }

    public void addOnlyAvailableFilter(JPAQuery<Event> query, Boolean onlyAvailable) {
        if (Boolean.TRUE.equals(onlyAvailable)) {
            QEventRequest request = QEventRequest.eventRequest;
            query.leftJoin(request)
                    .on(request.event.eq(qEntity).and(request.status.eq(EventRequestStatus.CONFIRMED)))
                    .groupBy(qEntity.id)
                    .having(request.count().lt(qEntity.participantLimit)
                            .or(qEntity.participantLimit.eq(0)));
        }
    }

    public void addPaidFilter(JPAQuery<Event> query, Boolean paid) {
        if (paid != null) {
            BooleanExpression paidFilter = paid ? qEntity.paid.isTrue() : qEntity.paid.isFalse();
            query.where(paidFilter);
        }
    }

    public void addCategoriesFilter(JPAQuery<Event> query, List<Long> categoriesIds) {
        if (categoriesIds != null && !categoriesIds.isEmpty()) {
            query.where(qEntity.category.id.in(categoriesIds));
        }
    }

    public void addStatesFilter(JPAQuery<Event> query, List<EventState> states) {
        if (states != null && !states.isEmpty()) {
            query.where(qEntity.state.in(states));
        }
    }

    public void addUsersFilter(JPAQuery<Event> query, List<Long> userIds) {
        if (userIds != null && !userIds.isEmpty()) {
            query.where(qEntity.initiator.id.in(userIds));
        }
    }

    public void addTimeRangeFilter(JPAQuery<Event> query, TimeRangeRequest timeRangeRequest) {
        LocalDateTime rangeStart = timeRangeRequest.getRangeStart();
        if (rangeStart != null) {
            query.where(qEntity.eventDate.goe(rangeStart));
        }

        LocalDateTime rangeEnd = timeRangeRequest.getRangeEnd();
        if (rangeEnd != null) {
            query.where(qEntity.eventDate.loe(rangeEnd));
        }
    }

    public void addFutureDateFilter(JPAQuery<Event> query) {
        query.where(qEntity.eventDate.gt(LocalDateTime.now()));
    }

    public void addOrderByEventDate(JPAQuery<Event> query) {
        query.orderBy(qEntity.eventDate.desc());
    }

    private List<Event> filterLocationWithRadius(List<Event> events,
                                                 LocationRadiusRequest locationRadiusRequest) {
        Double lat = locationRadiusRequest.getLat();
        Double lon = locationRadiusRequest.getLon();

        if (lat != null && lon != null) {
            final double radius = locationRadiusRequest.getRadius() != null
                    ? locationRadiusRequest.getRadius()
                    : DEFAULT_LOCATION_RADIUS;

            events = events.stream()
                    .filter(event ->
                            geoUtils.calculateDistance(
                                    event.getLocation().getLat(), event.getLocation().getLon(), lat, lon
                            ) <= radius
                    ).collect(Collectors.toList());
        }
        return events;
    }

    private List<Event> applyPagination(List<Event> events, PaginationRequest paginationRequest) {
        Integer from = paginationRequest.getFrom();
        Integer size = paginationRequest.getSize();
        return events.stream()
                .skip(from == null ? DEFAULT_PAGINATION_FROM : from)
                .limit(size == null ? DEFAULT_PAGINATION_SIZE : size)
                .collect(Collectors.toList());
    }
}
