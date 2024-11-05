package org.java4me.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class VideoStorageConfiguration {
    @Value("${app.video.portion-size}")
    private final String PORTION_SIZE;

    @Bean
    public Long portionSize() {
        if (!PORTION_SIZE.matches("\\d+[GMK]?[bB]"))
            throw new RuntimeException("Portion size wrong declaration");

        int end = PORTION_SIZE.length() - 1;

        char curr = PORTION_SIZE.charAt(end - 1);
        long multiplier = 1;
        if (curr == 'G') {
            multiplier *= 1024;
            curr = 'M';
        } if (curr == 'M') {
            multiplier *= 1024;
            curr = 'K';
        } if (curr == 'K') {
            multiplier *= 1024;
            end--;
        }

        return Long.parseLong(PORTION_SIZE.substring(0, end)) * multiplier;
    }
}
