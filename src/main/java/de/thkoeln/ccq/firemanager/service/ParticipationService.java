package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.model.Appointment;
import de.thkoeln.ccq.firemanager.model.User;
import de.thkoeln.ccq.firemanager.repository.AppointmentRepository;
import de.thkoeln.ccq.firemanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public void addParticipants(String appointmentId, List<String> userIds) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        Set<User> usersToAdd = userIds.stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + userId)))
                .collect(Collectors.toSet());
        
        appointment.getParticipants().addAll(usersToAdd);
        appointmentRepository.save(appointment);
    }

    public void removeParticipants(String appointmentId, List<String> userIds) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        Set<User> usersToRemove = userIds.stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + userId)))
                .collect(Collectors.toSet());
        
        appointment.getParticipants().removeAll(usersToRemove);
        appointmentRepository.save(appointment);
    }

    public void acceptParticipation(String appointmentId, String userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // TODO: Implement participation status tracking
        // This would involve adding a participation status field to the join table
        // For now, we just ensure the user is a participant
        appointment.getParticipants().add(user);
        appointmentRepository.save(appointment);
    }

    public void declineParticipation(String appointmentId, String userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // TODO: Implement participation status tracking
        // This would involve adding a participation status field to the join table
        // For now, we just ensure the user is not a participant
        appointment.getParticipants().remove(user);
        appointmentRepository.save(appointment);
    }

    public String getParticipationStatus(String appointmentId, String userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // TODO: Implement participation status tracking
        // This would involve checking the participation status from the join table
        // For now, we return a default status
        if (appointment.getParticipants().contains(user)) {
            return "ACCEPTED";
        } else {
            return "UNDECIDED";
        }
    }
}
