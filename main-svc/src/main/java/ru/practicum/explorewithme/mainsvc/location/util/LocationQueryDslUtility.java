package ru.practicum.explorewithme.mainsvc.location.util;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.common.requests.LocationRadiusRequest;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.querydsl.QueryDslUtility;
import ru.practicum.explorewithme.mainsvc.location.entity.Location;
import ru.practicum.explorewithme.mainsvc.location.entity.QLocation;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocationQueryDslUtility extends QueryDslUtility<Location, QLocation> {
    private static final double DEFAULT_LOCATION_RADIUS = 3.0; // default value for location radius if it's not provided
    private static final int DEFAULT_PAGINATION_FROM = 0; // default value for pagination from if it's not provided
    private static final int DEFAULT_PAGINATION_SIZE = 10; // default value for pagination size if it's not provided

    private final GeoUtils geoUtils;

    @Autowired
    public LocationQueryDslUtility(GeoUtils geoUtils) {
        super(QLocation.location);
        this.geoUtils = geoUtils;
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

    public List<Location> getQueryResultWithLocationAndPaginationFilters(JPAQuery<Location> query,
                                                                         LocationRadiusRequest locationRadiusRequest,
                                                                         PaginationRequest paginationRequest) {
        List<Location> locations = getQueryResultWithFetchJoins(query);
        locations = filterLocationWithRadius(locations, locationRadiusRequest);
        locations = applyPagination(locations, paginationRequest);
        return locations;
    }

    public void addTextSearchFilter(JPAQuery<Location> query, String text) {
        if (text != null && !text.isBlank()) {
            String sqlText = "%" + text.toLowerCase() + "%";
            query.where(qEntity.name.lower().like(sqlText));
        }
    }

    public void addVerifiedFilter(JPAQuery<Location> query, Boolean verified) {
        query.where(qEntity.verified.eq(verified != null && verified));
    }

    private List<Location> filterLocationWithRadius(List<Location> locations,
                                                    LocationRadiusRequest locationRadiusRequest) {
        Double lat = locationRadiusRequest.getLat();
        Double lon = locationRadiusRequest.getLon();

        if (lat != null && lon != null) {
            final double radius = locationRadiusRequest.getRadius() != null
                    ? locationRadiusRequest.getRadius()
                    : DEFAULT_LOCATION_RADIUS;

            locations = locations.stream()
                    .filter(location ->
                            geoUtils.calculateDistance(location.getLat(), location.getLon(), lat, lon) <= radius
                    ).collect(Collectors.toList());
        }
        return locations;
    }

    private List<Location> applyPagination(List<Location> locations, PaginationRequest paginationRequest) {
        Integer from = paginationRequest.getFrom();
        Integer size = paginationRequest.getSize();
        return locations.stream()
                .skip(from == null ? DEFAULT_PAGINATION_FROM : from)
                .limit(size == null ? DEFAULT_PAGINATION_SIZE : size)
                .collect(Collectors.toList());
    }
}
