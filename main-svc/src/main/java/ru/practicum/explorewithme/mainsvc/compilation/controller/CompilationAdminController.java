package ru.practicum.explorewithme.mainsvc.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationCreationDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationUpdateDto;
import ru.practicum.explorewithme.mainsvc.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid CompilationCreationDto compilationCreationDto) {
        log.info("Create compilation (/admin/compilations POST). Body : {}", compilationCreationDto);
        return compilationService.addCompilation(compilationCreationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compId) {
        log.info("Delete compilation with id {} (/admin/compilations/{} DELETE).", compId, compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable long compId,
                                            @RequestBody @Valid CompilationUpdateDto compilationUpdateDto) {
        log.info("Update compilation with id {} (/admin/compilations/{} PATCH). Body : {}",
                compId, compId, compilationUpdateDto);
        return compilationService.updateCompilation(compId, compilationUpdateDto);
    }
}
