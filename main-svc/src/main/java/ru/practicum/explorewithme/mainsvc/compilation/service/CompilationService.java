package ru.practicum.explorewithme.mainsvc.compilation.service;

import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationCreationDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationUpdateDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationsRequest;
import ru.practicum.explorewithme.mainsvc.compilation.entity.Compilation;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(CompilationCreationDto dto);

    void deleteCompilation(long compId);

    CompilationDto updateCompilation(long compId, CompilationUpdateDto dto);

    CompilationDto getCompilationById(long compId);

    List<CompilationDto> getCompilations(CompilationsRequest compilationsRequest, PaginationRequest paginationRequest);

    Compilation findCompilationById(Long id);
}
