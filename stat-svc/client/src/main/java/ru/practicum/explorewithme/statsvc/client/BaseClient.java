package ru.practicum.explorewithme.statsvc.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BaseClient {
    protected final RestTemplate rest;

    protected ResponseEntity<?> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<?> post(
            String path, T body) {
        return post(path, null, body);
    }

    protected <T> ResponseEntity<?> post(
            String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, body);
    }

    private <T> ResponseEntity<?> makeAndSendRequest(
            HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        if (parameters != null && !parameters.isEmpty()) {
            path = getParametrizedPath(parameters, path);
        }

        ResponseEntity<Object> serviceResponse;
        try {
            if (parameters != null) {
                serviceResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                serviceResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return serviceResponse;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private String getParametrizedPath(final Map<String, Object> parameters, String path) {
        int counter = 0;
        StringBuilder pathBuilder = new StringBuilder(path);
        for (String key : parameters.keySet()) {
            pathBuilder.append(counter++ == 0 ? "?" : "&");
            pathBuilder.append(key).append("={").append(key).append("}");
        }
        return pathBuilder.toString();
    }
}
