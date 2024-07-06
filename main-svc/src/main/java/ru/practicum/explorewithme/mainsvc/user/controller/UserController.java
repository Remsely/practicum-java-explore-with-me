package ru.practicum.explorewithme.mainsvc.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.user.dto.UserDto;
import ru.practicum.explorewithme.mainsvc.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        log.info("Create user (/admin/users POST). Body : {}", userDto);
        return userService.addUser(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {
        log.info("Delete user with id {} (/admin/users/{} DELETE).", userId, userId);
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@ModelAttribute @Validated PaginationRequest request,
                                  @RequestParam(required = false) List<Long> ids) {
        log.info("Get users by admin (/admin/users?from={}&size={}&ids={} GET).",
                request.getFrom(), request.getSize(), ids);
        return userService.getUsers(request, ids);
    }
}
