package ru.practicum.explorewithme.mainsvc.user.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.EntityByIdExistenceGuard;
import ru.practicum.explorewithme.mainsvc.exception.AlreadyExistsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.user.entity.User;
import ru.practicum.explorewithme.mainsvc.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserGuardService implements EntityByIdExistenceGuard<User, Long> {
    private final UserRepository userRepository;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id = " + id + " not found.")
        );
    }

    @Override
    public void checkExistenceById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id = " + id + " not found.");
        }
    }

    public void checkEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExistsException("User with email = " + email + " already exists.");
        }
    }
}
