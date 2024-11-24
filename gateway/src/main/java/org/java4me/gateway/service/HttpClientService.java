package org.java4me.gateway.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class HttpClientService {
    private final WebClient webClient;

    /**
     * Универсальный метод для отправки HTTP-запросов с использованием WebClient.
     *
     * @param method         HTTP метод запроса (POST, GET, и т.д.)
     * @param url            URL для отправки запроса
     * @param headers        Заголовки запроса
     * @param body           Тело запроса (может быть MultiValueMap для multipart)
     * @param typeReference  Тип ожидаемого ответа
     * @param request        HttpServletRequest для построения нового Location заголовка при редиректе
     * @param <T>            Тип тела запроса
     * @param <R>            Тип тела ответа
     * @return ResponseEntity с телом ответа или редиректом
     * @throws IOException если происходит ошибка ввода-вывода
     */
    public <T, R> ResponseEntity<R> sendRequest(HttpMethod method,
                                                String url,
                                                HttpHeaders headers,
                                                T body,
                                                ParameterizedTypeReference<R> typeReference,
                                                HttpServletRequest request) throws IOException {

        WebClient.RequestBodySpec requestSpec = webClient.method(method)
                .uri(url)
                .headers(httpHeaders -> httpHeaders.addAll(headers));

        // Отправляем запрос и получаем ClientResponse
        Mono<ClientResponse> responseMono;

        if (body instanceof MultiValueMap) {
            responseMono = requestSpec
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData((MultiValueMap<String, ?>) body))
                    .exchangeToMono(Mono::just);
        } else if (body != null) {
            responseMono = requestSpec
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(body))
                    .exchangeToMono(Mono::just);
        } else {
            responseMono = requestSpec
                    .exchangeToMono(Mono::just);
        }
        ClientResponse response = responseMono.block();

        if (response == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (response.statusCode().is3xxRedirection()) {
            // Получаем заголовок Location из ответа
            URI originalLocation = response.headers().asHttpHeaders().getLocation();
            if (originalLocation != null) {
                // Строим базовый URL прокси
                String proxyBaseUrl = ServletUriComponentsBuilder.fromRequest(request)
                        .replacePath("")
                        .build()
                        .toUriString();

                // Строим новый Location, указывая на прокси
                URI newLocation;
                if (originalLocation.isAbsolute()) {
                    // Если Location абсолютный, заменяем схему, хост и порт на прокси
                    newLocation = ServletUriComponentsBuilder.fromUri(originalLocation)
                            .scheme(request.getScheme())
                            .host(request.getServerName())
                            .port(request.getServerPort())
                            .build()
                            .toUri();
                } else {
                    // Если Location относительный, строим абсолютный на основе прокси
                    newLocation = ServletUriComponentsBuilder.fromUriString(proxyBaseUrl)
                            .path(originalLocation.getPath())
                            .build()
                            .toUri();
                }

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setLocation(newLocation);

                return new ResponseEntity<>(responseHeaders, HttpStatus.FOUND);
            }
        }

        // Для остальных статусов, получаем тело ответа
        R responseBody = response.bodyToMono(typeReference).block();

        // Копируем заголовки из ответа
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.putAll(response.headers().asHttpHeaders());

        return new ResponseEntity<>(responseBody, responseHeaders, response.statusCode());
    }
}
