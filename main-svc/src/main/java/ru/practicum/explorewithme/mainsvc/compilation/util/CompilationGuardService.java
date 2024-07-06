package ru.practicum.explorewithme.mainsvc.compilation.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.EntityByIdExistenceGuard;
import ru.practicum.explorewithme.mainsvc.compilation.entity.Compilation;
import ru.practicum.explorewithme.mainsvc.compilation.repository.CompilationRepository;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class CompilationGuardService implements EntityByIdExistenceGuard<Compilation, Long> {
    private final CompilationRepository compilationRepository;

    @Override
    public Compilation findById(Long id) {
        return compilationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Compilation with id = " + id + " not found.")
        );
    }

    @Override
    public void checkExistenceById(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new EntityNotFoundException("Compilation with id = " + id + " not found.");
        }
    }
}
