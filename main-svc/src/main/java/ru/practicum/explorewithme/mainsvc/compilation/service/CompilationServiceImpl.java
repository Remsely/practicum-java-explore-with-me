package ru.practicum.explorewithme.mainsvc.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationCreationDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.mainsvc.compilation.dto.CompilationUpdateDto;
import ru.practicum.explorewithme.mainsvc.compilation.entity.Compilation;
import ru.practicum.explorewithme.mainsvc.compilation.mapper.CompilationMapper;
import ru.practicum.explorewithme.mainsvc.compilation.repository.CompilationRepository;
import ru.practicum.explorewithme.mainsvc.compilation.util.CompilationExceptionThrower;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event.util.EventExceptionThrower;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationExceptionThrower compilationExceptionThrower;
    private final CompilationMapper compilationMapper;

    private final EventExceptionThrower eventExceptionThrower;

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
        compilationExceptionThrower.checkExistenceById(compId);
        compilationRepository.deleteById(compId);
        log.info("Compilation with id: {} has been deleted", compId);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(long compId, CompilationUpdateDto dto) {
        Compilation compilation = compilationExceptionThrower.findById(compId);

        updateCompilationProperties(compilation, dto);
        Compilation updatedCompilation = compilationRepository.save(compilation);

        CompilationDto result = compilationMapper.toDto(updatedCompilation);
        log.info("Compilation with id: {} has been updated", compId);
        return result;
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
            Set<Event> events = eventExceptionThrower.findByIdIn(eventsIds);
            compilation.setEvents(events);
        }
    }
}
