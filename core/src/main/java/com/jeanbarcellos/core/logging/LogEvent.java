package com.jeanbarcellos.core.logging;

import java.time.Instant;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogEvent {

    private String event;
    private String level;

    private String message;

    private Map<String, Object> data;

    private Instant timestamp;
}