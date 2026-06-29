package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.AutoAssignRequestDTO;
import de.thkoeln.ccq.firemanager.service.AutoAssignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auto-assign")
@RequiredArgsConstructor
public class AutoAssignController {

    private final AutoAssignService autoAssignService;

    @PostMapping
    public ResponseEntity<Void> autoAssign(@RequestBody AutoAssignRequestDTO request) {
        autoAssignService.autoAssign(request.getAppointmentId(), request.getRuleId());
        return ResponseEntity.ok().build();
    }
}
