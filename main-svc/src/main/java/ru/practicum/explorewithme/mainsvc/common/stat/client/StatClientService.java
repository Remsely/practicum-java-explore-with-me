package ru.practicum.explorewithme.mainsvc.common.stat.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.statsvc.client.StatClient;
import ru.practicum.explorewithme.statsvc.common.dto.HitDto;
import ru.practicum.explorewithme.statsvc.common.dto.StatDto;
import ru.practicum.explorewithme.statsvc.common.dto.StatsRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatClientService {
    @Value("${spring.application.name}")
    private static final String APP_NAME = "ewm-main-service";

    private static final String EVENT_URI = "/events/";

    private final ObjectMapper objectMapper;
    private final StatClient statClient;

    public void sendStat(HttpServletRequest request) {
        HitDto dto = HitDto.builder()
                .app(APP_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        log.info("Stat has been sent : {}", dto);
        statClient.postHit(dto);
    }

    public List<StatDto> getEventsStats(List<Long> eventIds, LocalDateTime start, LocalDateTime end) {
        List<String> uris = eventIds == null || eventIds.isEmpty()
                ? null : eventIds.stream()
                .map(id -> EVENT_URI + id)
                .collect(Collectors.toList());
        StatsRequest request = StatsRequest.builder()
                .uris(uris)
                .start(start)
                .end(end)
                .unique(true)
                .build();
        ResponseEntity<?> response = statClient.getStats(request);
        List<StatDto> statDtos = convertToStatDtoList(response);
        log.info("Stats has been received. {}", statDtos.size() > 3
                ? "List size : " + statDtos.size() + "."
                : "List : " + statDtos + ".");
        return convertToStatDtoList(response);
    }

    private List<StatDto> convertToStatDtoList(ResponseEntity<?> response) {
        if (response.getStatusCode().isError()) {
            return List.of();
        }
        try {
            String json = objectMapper.writeValueAsString(response.getBody());
            TypeReference<List<StatDto>> typeRef = new TypeReference<>() {
            };
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.warn("Error converting stat response : ", e);
            return List.of();
        }
    }
}
