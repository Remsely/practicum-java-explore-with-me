package ru.practicum.explorewithme.mainsvc.compilation.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
@Builder
public class CompilationUpdateDto {
    private Set<Long> events;

    private Boolean pinned;

    @Length(min = 3, max = 50)
    private String title;
}
