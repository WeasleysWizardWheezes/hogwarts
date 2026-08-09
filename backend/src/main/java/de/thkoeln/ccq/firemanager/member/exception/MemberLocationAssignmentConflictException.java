package de.thkoeln.ccq.firemanager.member.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceConflictException;

public class MemberLocationAssignmentConflictException extends ResourceConflictException {
    public MemberLocationAssignmentConflictException(String message) {
        super(message);
    }
}