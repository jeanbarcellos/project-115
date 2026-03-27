package com.jeanbarcellos.project115.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String CORRELATION_ID_KEY = "correlationId";

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

}
