package ru.practicum.explorewithme.statsvc.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.statsvc.common.dto.HitDto;
import ru.practicum.explorewithme.statsvc.common.dto.StatsRequest;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatClient extends BaseClient {
    @Autowired
    public StatClient(@Value("${stat-svc.server.url}") String url, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                .build());
    }

    public ResponseEntity<?> postHit(HitDto dto) {
        return post("/hit", dto);
    }

    public ResponseEntity<?> getStats(StatsRequest request) {
        Map<String, Object> params = new HashMap<>();
        if (request.getUnique() != null) {
            params.put("unique", request.getUnique());
        }
        if (request.getUris() != null && !request.getUris().isEmpty()) {
            params.put("uris", String.join(",", request.getUris()));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = request.getStart().format(formatter);
        String end = request.getEnd().format(formatter);

        params.put("start", start);
        params.put("end", end);
        return get("/stats", params);
    }
}
