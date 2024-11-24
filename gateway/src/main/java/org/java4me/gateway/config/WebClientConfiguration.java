package org.java4me.gateway.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient webClient() {
        var httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMinutes(5)) // Таймаут ожидания ответа
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300000) // Таймаут подключения
                .followRedirect(false);


        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
