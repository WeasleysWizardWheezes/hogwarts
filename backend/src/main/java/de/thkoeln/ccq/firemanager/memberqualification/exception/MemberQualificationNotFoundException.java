package de.thkoeln.ccq.firemanager.memberqualification.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceNotFoundException;

import java.util.UUID;

public class MemberQualificationNotFoundException extends ResourceNotFoundException {
    public MemberQualificationNotFoundException(UUID id) {
        super("MemberQualification", id);
    }
}
