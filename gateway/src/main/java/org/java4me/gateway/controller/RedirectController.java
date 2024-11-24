package org.java4me.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java4me.gateway.dto.VideoCreateEditDto;
import org.java4me.gateway.service.HttpClientService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RedirectController {

    private final WebClient webClient;
    private final RestTemplate restTemplate;
    private final HttpClientService httpClientService;

    private static final String TARGET_BASE_URL = "http://localhost:8081";

    @PostMapping("/videos/create")
    public ResponseEntity<String> create(VideoCreateEditDto video,
                                               HttpServletRequest request) throws IOException {

        var builder = new MultipartBodyBuilder();
        builder.part("video", new InputStreamResource(video.getVideo().getInputStream()) {
            @Override
            public String getFilename() {
                return video.getVideo().getOriginalFilename(); // Указываем оригинальное имя файла
            }

            @Override
            public long contentLength() throws IOException {
                return video.getVideo().getSize(); // Возвращаем размер файла
            }
        });

        builder.part("name", video.getName());
        // Добавьте другие поля по необходимости

        // Строим тело запроса
        var multipartData = builder.build();

        var headers = new HttpHeaders();

        return httpClientService.sendRequest(HttpMethod.POST,
                TARGET_BASE_URL + "/videos/create",
                headers,
                multipartData,
                ParameterizedTypeReference.forType(String.class),
                request);

//        // Отправляем POST-запрос на второй микросервис
//        return webClient.post()
//                .uri(TARGET_BASE_URL + "/videos/create")
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .body(BodyInserters.fromMultipartData(multipartData))
//                .retrieve()
//                .toEntity(String.class);
//        return sendRequest(HttpMethod.POST,
//                TARGET_BASE_URL + "/videos/create",
//                headers,
//                multipartRequest,
//                ParameterizedTypeReference.forType(String.class));
    }


    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxy(
            HttpMethod method,
            @RequestHeader HttpHeaders headers,
            HttpServletRequest request,
            @RequestBody(required = false) byte[] body) {

        // Построение целевого URL
        String uri = TARGET_BASE_URL + request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        System.out.println(uri);

        // Копирование заголовков, исключая Host
        HttpHeaders proxyHeaders = new HttpHeaders();
        proxyHeaders.putAll(headers);
        proxyHeaders.remove(HttpHeaders.HOST);

        return sendRequest(method,
                uri,
                proxyHeaders,
                body,
                ParameterizedTypeReference.forType(byte[].class));
    }

    private <T, R> ResponseEntity<R> sendRequest(HttpMethod method,
                                                 String url,
                                                 HttpHeaders headers,
                                                 T body,
                                                 ParameterizedTypeReference<R> typeReference) {
        // Создание сущности запроса
        HttpEntity<T> httpEntity = new HttpEntity<>(body, headers);

        // Выполнение запроса
        return restTemplate.exchange(
                url,
                method,
                httpEntity,
                typeReference
        );
    }

}
