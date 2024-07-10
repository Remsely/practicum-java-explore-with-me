package ru.practicum.explorewithme.mainsvc.compilation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationCreationDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.mainsvc.compilation.entity.Compilation;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.mapper.EventMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public Compilation toEntity(CompilationCreationDto dto) {
        return Compilation.builder()
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    public CompilationDto toDto(Compilation compilation) {
        Set<Event> events = compilation.getEvents();
        return CompilationDto.builder()
                .events(events == null ? new HashSet<>() : eventMapper.toShortDtoList(events))
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public List<CompilationDto> toDtoList(List<Compilation> compilations) {
        return compilations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
