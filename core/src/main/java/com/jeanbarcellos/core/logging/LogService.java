package com.jeanbarcellos.core.logging;

import java.util.Map;

public interface LogService {

    void info(LogEventType event, String message, Map<String, Object> data);

    void warn(LogEventType event, String message, Map<String, Object> data);

    void error(LogEventType event, String message, Map<String, Object> data, Throwable ex);
}