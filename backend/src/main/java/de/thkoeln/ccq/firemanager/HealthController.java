package de.thkoeln.ccq.firemanager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public HealthResponse check() {
        return new HealthResponse("ok", System.currentTimeMillis(), "1.0.0");
    }

    @GetMapping("/{component}")
    public ComponentHealth checkComponent(@PathVariable String component) {
        return new ComponentHealth(component, "healthy");
    }

    public record HealthResponse(String status, long timestamp, String version) {}

    public record ComponentHealth(String component, String status) {}
}
