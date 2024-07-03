package ru.practicum.explorewithme.mainsvc.common.utils.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.event.entity.LocationPrimaryKey;
import ru.practicum.explorewithme.mainsvc.event.entity.Location;
import ru.practicum.explorewithme.mainsvc.event.repository.LocationRepository;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LocationRepositoryHelper implements RepositoryByIdHelper<Location, LocationPrimaryKey> {
    private final LocationRepository locationRepository;

    @Override
    public Location findById(LocationPrimaryKey id) {
        return locationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ErrorResponseDto.builder()
                        .status(HttpStatus.NOT_FOUND.toString())
                        .reason("Location not found.")
                        .message("Location with id = " + id + " not found.")
                        .timestamp(LocalDateTime.now())
                        .build()
        ));
    }

    @Override
    public void checkExistenceById(LocationPrimaryKey id) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.NOT_FOUND.toString())
                            .reason("Location not found.")
                            .message("Location with id = " + id + " not found.")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }
}
