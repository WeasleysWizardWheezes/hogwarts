package com.hogwarts.operation;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class OperationCreatedEvent extends ApplicationEvent {
    private final UUID operationId;
    private final String title;
    private final String description;
    private final LocalDateTime startTime;

    public OperationCreatedEvent(Object source, UUID operationId, String title, String description, LocalDateTime startTime) {
        super(source);
        this.operationId = operationId;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
    }
}
