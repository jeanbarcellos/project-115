package com.jeanbarcellos.project115.infra.adapter;

import org.springframework.http.ProblemDetail;

import com.jeanbarcellos.core.apierror.ApiError;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpringProblemDetailMapper {

    public static ProblemDetail toProblemDetail(ApiError error) {
        ProblemDetail pd = ProblemDetail.forStatus(error.status());
        pd.setType(error.type());
        pd.setTitle(error.title());
        pd.setDetail(error.detail());
        pd.setInstance(error.instance());

        error.properties().forEach(pd::setProperty);

        return pd;
    }
}
