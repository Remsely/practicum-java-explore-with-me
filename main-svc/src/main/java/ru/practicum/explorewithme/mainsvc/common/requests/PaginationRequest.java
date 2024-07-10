package ru.practicum.explorewithme.mainsvc.common.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@Getter
@Setter
public class PaginationRequest {
    @PositiveOrZero(message = "From index must not be negative.")
    private Integer from;

    @Positive
    private Integer size;
}
