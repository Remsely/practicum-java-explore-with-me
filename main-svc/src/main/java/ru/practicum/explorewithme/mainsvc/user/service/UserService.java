package ru.practicum.explorewithme.mainsvc.user.service;

import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.user.dto.UserDto;
import ru.practicum.explorewithme.mainsvc.user.entity.User;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    void deleteUser(Long userId);

    List<UserDto> getUsers(PaginationRequest paginationRequest, List<Long> ids);

    User findUserById(Long id);
}
