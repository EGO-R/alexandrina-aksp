package org.java4me.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RedirectController {
    private final WebClient webClient;

    // Базовый URL целевого микросервиса
    private final String TARGET_BASE_URL = "http://localhost:8081";

//    @RequestMapping("/api/v1/videos/{id}")
@RequestMapping("/**")

    public Mono<ResponseEntity<InputStreamResource>> getVideoRedirect(HttpMethod method,
                                                           @RequestHeader HttpHeaders headers,
                                                           @RequestBody(required = false) byte[] body,
                                                           HttpServletRequest request) {
        return proxy(method, headers, body, request, ParameterizedTypeReference.forType(InputStreamResource.class));
    }

//    @RequestMapping("/**")
    public Mono<ResponseEntity<byte[]>> defaultRedirect(HttpMethod method,
                                                        @RequestHeader HttpHeaders headers,
                                                        @RequestBody(required = false) byte[] body,
                                                        HttpServletRequest request) {
        return proxy(method, headers, body, request, ParameterizedTypeReference.forType(byte[].class));
    }

    public <G, R> Mono<ResponseEntity<R>> proxy(HttpMethod method,
                                              HttpHeaders headers,
                                              G body,
                                              HttpServletRequest request,
                                                ParameterizedTypeReference<R> type) {
        log.info("redirecting...");

        // Построение целевого URL
        String uri = TARGET_BASE_URL + request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        // Создание запроса к целевому микросервису
        WebClient.RequestBodySpec requestSpec = webClient.method(method)
                .uri(uri)
                .headers(httpHeaders -> {
                    httpHeaders.addAll(headers);
                    httpHeaders.remove(HttpHeaders.HOST);
                });

        // Добавление тела запроса, если оно есть
        var requestBodySpec = (body != null)
                ? requestSpec.bodyValue(body)
                : requestSpec;

        // Отправка запроса и получение ответа
        return requestBodySpec.exchangeToMono(clientResponse -> {
            HttpHeaders responseHeaders = new HttpHeaders();
            clientResponse.headers().asHttpHeaders().forEach((key, value) -> {
                responseHeaders.put(key, value);
            });

            return clientResponse.bodyToMono(type)
                    .map(bodyBytes -> new ResponseEntity<>(bodyBytes, responseHeaders, clientResponse.statusCode()));
        });
    }


}
