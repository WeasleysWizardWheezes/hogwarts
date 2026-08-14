package de.thkoeln.ccq.firemanager.memberqualification.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceConflictException;

public class MemberQualificationConflictException extends ResourceConflictException {
    public MemberQualificationConflictException(String message) {
        super(message);
    }
}
