package ru.practicum.explorewithme.mainsvc.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.pageable.PageableUtility;
import ru.practicum.explorewithme.mainsvc.exception.AlreadyExistsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.user.dto.UserDto;
import ru.practicum.explorewithme.mainsvc.user.entity.User;
import ru.practicum.explorewithme.mainsvc.user.mapper.UserMapper;
import ru.practicum.explorewithme.mainsvc.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PageableUtility pageableUtility;

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        String email = userDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExistsException("User with email = " + email + " already exists.");
        }

        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);

        UserDto dto = userMapper.toDto(savedUser);
        log.info("User has been saved : {}", dto);
        return dto;
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " not found.");
        }
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

    @Transactional(readOnly = true)
    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id = " + id + " not found.")
        );
    }
}
