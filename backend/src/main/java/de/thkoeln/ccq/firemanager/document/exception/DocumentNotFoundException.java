package de.thkoeln.ccq.firemanager.document.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceNotFoundException;

import java.util.UUID;

public class DocumentNotFoundException extends ResourceNotFoundException {
    public DocumentNotFoundException(UUID id) {
        super("Document", id);
    }
}