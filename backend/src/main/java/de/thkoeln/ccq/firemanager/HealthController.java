package de.thkoeln.ccq.firemanager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public HealthResponse check() {
        return new HealthResponse("ok", System.currentTimeMillis());
    }

    public record HealthResponse(String status, long timestamp) {}
}
