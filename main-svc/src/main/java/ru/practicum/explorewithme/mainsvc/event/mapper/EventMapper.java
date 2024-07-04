package ru.practicum.explorewithme.mainsvc.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.category.mapper.CategoryMapper;
import ru.practicum.explorewithme.mainsvc.event.dto.creation.EventCreationDto;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventFullDto;
import ru.practicum.explorewithme.mainsvc.event.dto.info.EventShortDto;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.event_request.entity.EventRequest;
import ru.practicum.explorewithme.mainsvc.location.mapper.LocationMapper;
import ru.practicum.explorewithme.mainsvc.user.mapper.UserMapper;
import ru.practicum.explorewithme.statsvc.common.dto.StatDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;
    private final CategoryMapper categoryMapper;

    public Event toEntity(EventCreationDto dto) {
        return Event.builder()
                .annotation(dto.getAnnotation())
                .category(Category.builder()
                        .id(dto.getCategory())
                        .build())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .location(locationMapper.toEntity(dto.getLocation()))
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .build();
    }

    public EventFullDto toFullDto(Event event, int confirmedRequests, long views) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .initiator(userMapper.toDto(event.getInitiator()))
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(locationMapper.toDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public EventFullDto toFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toDto(event.getCategory()))
                .confirmedRequests(0)
                .createdOn(event.getCreatedOn())
                .initiator(userMapper.toDto(event.getInitiator()))
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(locationMapper.toDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(0L)
                .build();
    }

    public EventShortDto toShortDto(Event event, int confirmedRequests, long views) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(userMapper.toDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public List<EventShortDto> toShortDtoList(List<Event> events,
                                              List<EventRequest> requests,
                                              List<StatDto> stats) {
        Map<Long, Integer> confirmedRequestsByEventsIds = getConfirmedRequestsByEventsIds(requests);
        Map<Long, Long> viewsByEventIds = getViewsByEventIds(stats);
        return events.stream()
                .map(e -> toShortDto(
                        e,
                        confirmedRequestsByEventsIds.getOrDefault(e.getId(), 0),
                        viewsByEventIds.getOrDefault(e.getId(), 0L)
                )).collect(Collectors.toList());
    }

    public List<EventFullDto> toDtoList(List<Event> events,
                                        List<EventRequest> requests,
                                        List<StatDto> stats) {
        Map<Long, Integer> confirmedRequestsByEventsIds = getConfirmedRequestsByEventsIds(requests);
        Map<Long, Long> viewsByEventIds = getViewsByEventIds(stats);
        return events.stream()
                .map(e -> toFullDto(
                        e,
                        confirmedRequestsByEventsIds.getOrDefault(e.getId(), 0),
                        viewsByEventIds.getOrDefault(e.getId(), 0L)
                )).collect(Collectors.toList());
    }

    private Map<Long, Integer> getConfirmedRequestsByEventsIds(List<EventRequest> requests) {
        return requests.stream()
                .collect(Collectors.groupingBy(
                        request -> request.getEvent().getId(),
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                Long::intValue
                        )
                ));
    }

    private Map<Long, Long> getViewsByEventIds(List<StatDto> stats) {
        return stats.stream()
                .collect(Collectors.groupingBy(
                        stat -> {
                            String uri = stat.getUri();
                            int lastSlashIndex = uri.lastIndexOf('/');
                            return Long.parseLong(uri.substring(lastSlashIndex + 1));
                        },
                        Collectors.collectingAndThen(
                                Collectors.summingLong(StatDto::getHits),
                                Long::longValue
                        )
                ));
    }
}
