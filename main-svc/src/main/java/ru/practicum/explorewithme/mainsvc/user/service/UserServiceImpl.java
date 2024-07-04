package ru.practicum.explorewithme.mainsvc.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.pageable.PageableUtility;
import ru.practicum.explorewithme.mainsvc.user.dto.UserDto;
import ru.practicum.explorewithme.mainsvc.user.entity.User;
import ru.practicum.explorewithme.mainsvc.user.mapper.UserMapper;
import ru.practicum.explorewithme.mainsvc.user.repository.UserRepository;
import ru.practicum.explorewithme.mainsvc.user.util.UserExceptionThrower;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserExceptionThrower userExceptionThrower;
    private final UserRepository userRepository;
    private final PageableUtility pageableUtility;

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        userExceptionThrower.checkEmailUniqueness(userDto.getEmail());

        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);

        UserDto dto = userMapper.toDto(savedUser);
        log.info("User has been saved : {}", dto);
        return dto;
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userExceptionThrower.checkExistenceById(userId);
        userRepository.deleteById(userId);
        log.info("User with id = {} has been deleted.", userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(PaginationRequest paginationRequest, List<Long> ids) {
        Pageable pageable = pageableUtility.toPageable(paginationRequest);

        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageable).toList();
        } else {
            users = userRepository.findByIdIn(ids, pageable);
        }
        List<UserDto> dtos = userMapper.toDtoList(users);
        log.info("Users have been found. List size : {}", dtos.size());
        return dtos;
    }
}
