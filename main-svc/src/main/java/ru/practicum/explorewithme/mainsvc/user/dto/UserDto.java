package ru.practicum.explorewithme.mainsvc.user.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDto {
    private Long id;

    @NotBlank(message = "User name must not be blank.")
    @Length(min = 2, max = 250, message = "User name must be between 2 and 250 characters.")
    private String name;

    @NotBlank(message = "User email must not be blank.")
    @Size(min = 6, max = 254, message = "User email must be between 6 and 250 characters.")
    @Email(message = "User email is not valid.")
    private String email;
}
