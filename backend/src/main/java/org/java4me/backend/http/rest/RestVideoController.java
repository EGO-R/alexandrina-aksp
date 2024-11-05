package org.java4me.backend.http.rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.java4me.backend.service.VideoStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/videos")
public class RestVideoController {
    private final VideoStorageService videoStorageService;

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getVideo(@PathVariable("id") Long id,
                                             HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();

        String range = request.getHeader(HttpHeaders.RANGE);
        if (range != null) {
            var body = videoStorageService.getRange(id.toString(), range, headers);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(body);
        }

        var body = videoStorageService.get(id.toString(), headers);
        return ResponseEntity.ok()
                .headers(headers)
                .body(body);

    }
}
