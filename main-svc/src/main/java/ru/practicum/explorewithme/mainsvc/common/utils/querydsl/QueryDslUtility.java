package ru.practicum.explorewithme.mainsvc.common.utils.querydsl;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RequiredArgsConstructor
public abstract class QueryDslUtility<T, QT> {
    protected final QT qEntity;

    @PersistenceContext
    protected EntityManager em;

    public abstract JPAQuery<T> getQuery();

    public abstract List<T> getQueryResultWithFetchJoins(JPAQuery<T> query);

    public void addPaginationFilter(JPAQuery<T> query, PaginationRequest paginationRequest) {
        Integer from = paginationRequest.getFrom();
        Integer size = paginationRequest.getSize();
        query.offset(from == null ? 0 : from).limit(size == null ? 10 : size);
    }
}
