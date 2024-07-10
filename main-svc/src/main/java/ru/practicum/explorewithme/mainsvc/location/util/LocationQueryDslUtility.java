package ru.practicum.explorewithme.mainsvc.location.util;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.common.requests.LocationRadiusRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.querydsl.QueryDslUtility;
import ru.practicum.explorewithme.mainsvc.location.entity.Location;
import ru.practicum.explorewithme.mainsvc.location.entity.QLocation;

import java.util.List;

@Component
public class LocationQueryDslUtility extends QueryDslUtility<Location, QLocation> {
    public LocationQueryDslUtility() {
        super(QLocation.location);
    }

    @Override
    public JPAQuery<Location> getQuery() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        return queryFactory.selectFrom(qEntity);
    }

    @Override
    public List<Location> getQueryResultWithFetchJoins(JPAQuery<Location> query) {
        return query.fetch();
    }

    public void addTextSearchFilter(JPAQuery<Location> query, String text) {
        if (text != null && !text.isBlank()) {
            String sqlText = "%" + text.toLowerCase() + "%";
            query.where(qEntity.name.lower().like(sqlText));
        }
    }

    public void addVerifiedFilter(JPAQuery<Location> query, Boolean verified) {
        if (Boolean.TRUE.equals(verified)) {
            query.where(qEntity.verified.isTrue());
        } else {
            query.where(qEntity.verified.isFalse());
        }
    }

    public void addLocationFilter(JPAQuery<Location> query, LocationRadiusRequest locationRadiusRequest) {
        Double lat = locationRadiusRequest.getLat();
        Double lon = locationRadiusRequest.getLon();
        Double radius = locationRadiusRequest.getRadius();

        if (lat != null && lon != null) {
            if (radius == null) {
                radius = 3.0;
            }
            query.where(
                    Expressions.booleanTemplate(
                            "distance({0}, {1}, {2}, {3}) <= {4}",
                            lat, lon, qEntity.lat, qEntity.lon, radius
                    )
            );
        }
    }
}
