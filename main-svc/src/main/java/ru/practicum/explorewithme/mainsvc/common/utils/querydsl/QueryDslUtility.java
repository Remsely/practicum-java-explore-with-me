package ru.practicum.explorewithme.mainsvc.common.utils.querydsl;

import com.querydsl.jpa.impl.JPAQuery;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class QueryDslUtility<T, QT> {
    @PersistenceContext
    protected EntityManager em;

    public abstract JPAQuery<T> getQueryWithFetchJoins(QT qEntity);

    public void addPaginationFilter(JPAQuery<T> query, PaginationRequest paginationRequest) {
        Integer from = paginationRequest.getFrom();
        Integer size = paginationRequest.getSize();
        query.offset(from == null ? 0 : from).limit(size == null ? 10 : size);
    }
}
