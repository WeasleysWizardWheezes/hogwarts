package de.thkoeln.ccq.firemanager.location.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceNotFoundException;

import java.util.UUID;

public class LocationNotFoundException extends ResourceNotFoundException {
    public LocationNotFoundException(UUID id) {
        super("Location", id);
    }
}
