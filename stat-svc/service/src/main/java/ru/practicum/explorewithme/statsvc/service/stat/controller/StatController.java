package ru.practicum.explorewithme.statsvc.service.stat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.statsvc.common.dto.HitDto;
import ru.practicum.explorewithme.statsvc.common.dto.StatDto;
import ru.practicum.explorewithme.statsvc.common.dto.StatsRequest;
import ru.practicum.explorewithme.statsvc.service.stat.service.StatService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto postHit(@RequestBody @Valid HitDto dto) {
        return statService.commitHit(dto);
    }

    @GetMapping("/stats")
    public List<StatDto> getStats(@ModelAttribute StatsRequest request) {
        log.info(request.toString());
        return statService.getStats(request);
    }
}
