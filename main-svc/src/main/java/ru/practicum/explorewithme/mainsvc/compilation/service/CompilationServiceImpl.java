package ru.practicum.explorewithme.mainsvc.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationCreationDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationUpdateDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationsRequest;
import ru.practicum.explorewithme.mainsvc.compilation.entity.Compilation;
import ru.practicum.explorewithme.mainsvc.compilation.mapper.CompilationMapper;
import ru.practicum.explorewithme.mainsvc.compilation.repository.CompilationRepository;
import ru.practicum.explorewithme.mainsvc.compilation.util.CompilationQueryDslUtility;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.service.EventService;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final CompilationQueryDslUtility compilationQueryDslUtility;
    private final EventService eventService;

    @Transactional
    @Override
    public CompilationDto addCompilation(CompilationCreationDto dto) {
        Compilation compilation = compilationMapper.toEntity(dto);

        Set<Long> eventsIds = dto.getEvents();
        setNullableCompilationEvents(compilation, eventsIds);

        if (!Boolean.TRUE.equals(compilation.getPinned())) {
            compilation.setPinned(false);
        }

        Compilation savedCompilation = compilationRepository.save(compilation);
        CompilationDto result = compilationMapper.toDto(savedCompilation);
        log.info("Compilation has been saved : {}", result);
        return result;
    }

    @Transactional
    @Override
    public void deleteCompilation(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new EntityNotFoundException("Compilation with id = " + compId + " not found.");
        }
        compilationRepository.deleteById(compId);
        log.info("Compilation with id: {} has been deleted.", compId);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(long compId, CompilationUpdateDto dto) {
        Compilation compilation = this.findCompilationById(compId);

        updateCompilationProperties(compilation, dto);
        Compilation updatedCompilation = compilationRepository.save(compilation);

        CompilationDto result = compilationMapper.toDto(updatedCompilation);
        log.info("Compilation with id: {} has been updated.", compId);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = this.findCompilationById(compId);
        CompilationDto dto = compilationMapper.toDto(compilation);
        log.info("Compilation with id: {} has been found.", compId);
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getCompilations(CompilationsRequest compilationsRequest,
                                                PaginationRequest paginationRequest) {
        var query = compilationQueryDslUtility.getQuery();

        compilationQueryDslUtility.addPinnedFilter(query, compilationsRequest.getPinned());
        compilationQueryDslUtility.addPaginationFilter(query, paginationRequest);

        List<Compilation> compilations = compilationQueryDslUtility.getQueryResultWithFetchJoins(query);

        List<CompilationDto> dtos = compilationMapper.toDtoList(compilations);
        log.info("Compilations have been found. List size: {}", dtos.size());
        return dtos;
    }

    @Transactional(readOnly = true)
    @Override
    public Compilation findCompilationById(Long id) {
        return compilationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Compilation with id = " + id + " not found.")
        );
    }

    private void updateCompilationProperties(Compilation updating, CompilationUpdateDto updater) {
        Set<Long> eventsIds = updater.getEvents();
        setNullableCompilationEvents(updating, eventsIds);

        if (updater.getPinned() != null) {
            updating.setPinned(updater.getPinned());
        }

        if (updater.getTitle() != null) {
            updating.setTitle(updater.getTitle());
        }
    }

    private void setNullableCompilationEvents(Compilation compilation, Set<Long> eventsIds) {
        if (eventsIds != null && !eventsIds.isEmpty()) {
            Set<Event> events = eventService.findEventsByIdIn(eventsIds);
            compilation.setEvents(events);
        }
    }
}
