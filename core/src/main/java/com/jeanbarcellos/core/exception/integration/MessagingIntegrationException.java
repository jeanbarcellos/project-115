package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

public class MessagingIntegrationException extends IntegrationException {

    private final String topicOrQueue;
    private final String operation; // publish / consume

    public MessagingIntegrationException(
            String service,
            String topicOrQueue,
            String operation,
            String message,
            Map<String, Object> metadata,
            Throwable cause
    ) {
        super(service, null, message, null, metadata, cause);
        this.topicOrQueue = topicOrQueue;
        this.operation = operation;
    }

    public String getTopicOrQueue() {
        return topicOrQueue;
    }

    public String getOperation() {
        return operation;
    }
}