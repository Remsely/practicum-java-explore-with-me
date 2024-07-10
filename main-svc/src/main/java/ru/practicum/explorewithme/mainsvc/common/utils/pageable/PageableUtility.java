package ru.practicum.explorewithme.mainsvc.common.utils.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;

@Component
public class PageableUtility {
    public Pageable toPageable(PaginationRequest request) {
        Integer size = request.getSize();
        Integer from = request.getFrom();

        if (isPageableArgumentsNulls(from, size)) {
            return PageRequest.of(0, 10);
        }
        int page = from / size;
        return PageRequest.of(page, size);
    }

    private boolean isPageableArgumentsNulls(Integer from, Integer size) {
        return size == null || from == null;
    }
}
