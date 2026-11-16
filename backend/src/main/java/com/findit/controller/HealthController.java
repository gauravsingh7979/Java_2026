package com.findit.controller;

import com.findit.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.ok(
                Map.of("status", "UP", "platform", "FindIt Smart Lost & Found Platform", "version", "1.0.0"),
                "Service is running smoothly"
        ));
    }
}
