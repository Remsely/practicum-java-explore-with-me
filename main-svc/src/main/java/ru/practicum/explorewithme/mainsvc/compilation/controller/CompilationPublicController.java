package ru.practicum.explorewithme.mainsvc.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationsRequest;
import ru.practicum.explorewithme.mainsvc.compilation.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable long compId) {
        log.info("/compilations/{} GET", compId);
        return compilationService.getCompilationById(compId);
    }

    @GetMapping
    public List<CompilationDto> getCompilations(@ModelAttribute @Validated CompilationsRequest compilationsRequest,
                                                @ModelAttribute @Validated PaginationRequest paginationRequest) {
        log.info("/compilations GET");
        return compilationService.getCompilations(compilationsRequest, paginationRequest);
    }
}
