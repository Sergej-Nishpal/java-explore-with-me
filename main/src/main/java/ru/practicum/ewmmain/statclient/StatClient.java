package ru.practicum.ewmmain.statclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;
import ru.practicum.ewmmain.dto.incoming.ViewStats;
import ru.practicum.ewmmain.exception.StatClientException;
import ru.practicum.ewmmain.dto.EndpointHitDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatClient {
    private static final String STAT_URL = "http://localhost:9090";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final WebClient webClient;

    public StatClient() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(STAT_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        webClient = WebClient
                .builder()
                .uriBuilderFactory(factory)
                .baseUrl(STAT_URL)
                .build();
    }

    public void save(EndpointHitDto endpointHitDto) {
        webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(endpointHitDto), EndpointHitDto.class)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new StatClientException("Ошибка сохранения данных на сервере статистики!");
                })
                .toBodilessEntity()
                .block();
    }

    public Collection<ViewStats> getStats(LocalDateTime start, LocalDateTime end, Set<String> uris, boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", dateTimeFormatEncode(start))
                        .queryParam("end", dateTimeFormatEncode(end))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new StatClientException("Ошибка получения данных от сервера статистики!");
                })
                .bodyToMono(new ParameterizedTypeReference<Collection<ViewStats>>() {
                })
                .block();
    }

    private String dateTimeFormatEncode(LocalDateTime localDateTime) {
        return URLEncoder
                .encode(localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                        StandardCharsets.UTF_8);
    }
}