package ru.practicum.explorewithme.mainsvc.compilation.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@Builder
public class CompilationCreationDto {
    private Set<Long> events;

    private Boolean pinned;

    @NotBlank(message = "Compilation title must not be blank.")
    @Length(min = 3, max = 50, message = "Compilation title must be between 3 and 50 characters.")
    private String title;
}
