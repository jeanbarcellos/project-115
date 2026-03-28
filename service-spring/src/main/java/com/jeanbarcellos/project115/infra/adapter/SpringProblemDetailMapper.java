package com.jeanbarcellos.project115.infra.adapter;

import org.springframework.http.ProblemDetail;

import com.jeanbarcellos.core.error.ApiError;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpringProblemDetailMapper {

    public static ProblemDetail toProblemDetail(ApiError error) {

        ProblemDetail pd = ProblemDetail.forStatus(error.getStatus());
        pd.setType(error.getType());
        pd.setTitle(error.getTitle());
        pd.setDetail(error.getDetail());
        pd.setInstance(error.getInstance());

        error.getProperties().forEach(pd::setProperty);

        pd.setProperty("correlationId", error.getCorrelationId());
        pd.setProperty("timestamp", error.getTimestamp());

        return pd;
    }
}
