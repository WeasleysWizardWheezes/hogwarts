package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.model.Appointment;
import de.thkoeln.ccq.firemanager.model.Rule;
import de.thkoeln.ccq.firemanager.repository.AppointmentRepository;
import de.thkoeln.ccq.firemanager.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutoAssignService {

    private final AppointmentRepository appointmentRepository;
    private final RuleRepository ruleRepository;

    public void autoAssign(String appointmentId, String ruleId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        
        // TODO: Implement auto-assignment logic based on rule conditions and actions
        // This would involve evaluating the rule conditions and applying the actions
        // to automatically assign participants to the appointment
    }
}
