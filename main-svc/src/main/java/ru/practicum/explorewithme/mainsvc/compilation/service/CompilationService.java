package ru.practicum.explorewithme.mainsvc.compilation.service;

import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationCreationDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationUpdateDto;

public interface CompilationService {
    CompilationDto addCompilation(CompilationCreationDto dto);

    void deleteCompilation(long compId);

    CompilationDto updateCompilation(long compId, CompilationUpdateDto dto);
}
