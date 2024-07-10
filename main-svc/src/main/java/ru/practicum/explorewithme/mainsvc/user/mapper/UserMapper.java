package ru.practicum.explorewithme.mainsvc.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.user.dto.UserDto;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public User toEntity(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public List<UserDto> toDtoList(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
