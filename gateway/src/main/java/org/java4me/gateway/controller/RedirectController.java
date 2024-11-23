package org.java4me.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Mono;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RedirectController {

    private final WebClient webClient;
    private final RestTemplate restTemplate;

    private static final String TARGET_BASE_URL = "http://localhost:8081";


    @RequestMapping("/**")
    public ResponseEntity<StreamingResponseBody> proxy(
            HttpMethod method,
            @RequestHeader HttpHeaders headers,
            HttpServletRequest request,
            @RequestBody(required = false) byte[] body) throws Exception {

        // Построение целевого URL
        String uri = TARGET_BASE_URL + request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        // Копирование заголовков, исключая Host
        HttpHeaders proxyHeaders = new HttpHeaders();
        proxyHeaders.putAll(headers);
        proxyHeaders.remove(HttpHeaders.HOST);

        // Создание сущности запроса
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(body, proxyHeaders);

        // Выполнение запроса
        ResponseEntity<InputStreamResource> responseEntity = restTemplate.exchange(
                uri,
                method,
                httpEntity,
                InputStreamResource.class
        );

        // Создание StreamingResponseBody
        StreamingResponseBody stream = outputStream -> {
            try (InputStream inputStream = responseEntity.getBody().getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
        };

        // Возвращение ответа с потоковой передачей
        return ResponseEntity.status(responseEntity.getStatusCode())
                .headers(responseEntity.getHeaders())
                .body(stream);
    }

}
