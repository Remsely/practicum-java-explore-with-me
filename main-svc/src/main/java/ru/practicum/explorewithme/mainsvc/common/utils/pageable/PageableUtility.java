package ru.practicum.explorewithme.mainsvc.common.utils.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.exception.IllegalPageableArgumentsException;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

import java.time.LocalDateTime;

@Component
public class PageableUtility {
    public Pageable toPageable(PaginationRequest request) {
        Integer size = request.getSize();
        Integer from = request.getFrom();

        if (isPageableArgumentsNulls(from, size)) {
            return Pageable.unpaged();
        }
        int page = from / size;
        return PageRequest.of(page, size);
    }

    private boolean isPageableArgumentsNulls(Integer from, Integer size) {
        if (size == null && from == null) {
            return true;
        }
        if (size != null && from != null) {
            return false;
        }
        throw new IllegalPageableArgumentsException(ErrorResponseDto.builder()
                .status("BAD_REQUEST")
                .reason("Arguments 'from' and 'size' are illegal.")
                .message("Arguments 'from' and 'size' must be specified or not specified together.")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
}
