package ru.practicum.explorewithme.mainsvc.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.category.mapper.CategoryMapper;
import ru.practicum.explorewithme.mainsvc.event.dto.EventCreationDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventDto;
import ru.practicum.explorewithme.mainsvc.event.dto.EventShortDto;
import ru.practicum.explorewithme.mainsvc.event.entity.Event;
import ru.practicum.explorewithme.mainsvc.request.entity.Request;
import ru.practicum.explorewithme.mainsvc.user.mapper.UserMapper;

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

    public EventDto toDto(Event event, int confirmedRequests, long views) {
        return EventDto.builder()
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
                                              List<Request> requests,
                                              Map<Long, Long> viewsByEventId) {
        Map<Long, Integer> confirmedRequestsByEventsIds = getConfirmedRequestsByEventsIds(requests);
        return events.stream()
                .map(e -> toShortDto(
                        e,
                        confirmedRequestsByEventsIds.getOrDefault(e.getId(), 0),
                        viewsByEventId.getOrDefault(e.getId(), 0L)
                )).collect(Collectors.toList());
    }

    public List<EventDto> toDtoList(List<Event> events,
                                    List<Request> requests,
                                    Map<Long, Long> viewsByEventId) {
        Map<Long, Integer> confirmedRequestsByEventsIds = getConfirmedRequestsByEventsIds(requests);
        return events.stream()
                .map(e -> toDto(
                        e,
                        confirmedRequestsByEventsIds.getOrDefault(e.getId(), 0),
                        viewsByEventId.getOrDefault(e.getId(), 0L)
                )).collect(Collectors.toList());
    }

    private Map<Long, Integer> getConfirmedRequestsByEventsIds(List<Request> requests) {
        return requests.stream()
                .collect(Collectors.groupingBy(
                        request -> request.getEvent().getId(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }
}
