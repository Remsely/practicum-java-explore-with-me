package ru.practicum.explorewithme.mainsvc.compilation.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.ByIdExceptionThrower;
import ru.practicum.explorewithme.mainsvc.compilation.entity.Compilation;
import ru.practicum.explorewithme.mainsvc.compilation.repository.CompilationRepository;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CompilationExceptionThrower implements ByIdExceptionThrower<Compilation, Long> {
    private final CompilationRepository compilationRepository;

    @Override
    public Compilation findById(Long id) {
        return compilationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ErrorResponseDto.builder()
                        .status(HttpStatus.NOT_FOUND.toString())
                        .reason("Compilation not found.")
                        .message("Compilation with id: " + id + " not found.")
                        .timestamp(LocalDateTime.now())
                        .build()
        ));
    }

    @Override
    public void checkExistenceById(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.NOT_FOUND.toString())
                            .reason("Compilation not found.")
                            .message("Compilation with id: " + id + " not found.")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }
}
