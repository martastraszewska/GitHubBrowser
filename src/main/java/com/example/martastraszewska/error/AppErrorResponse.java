package com.example.martastraszewska.error;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public class AppErrorResponse implements ErrorResponse {

    private HttpStatusCode httpStatusCode;
    private ProblemDetail problemDetail;

    public AppErrorResponse(HttpStatusCode httpStatusCode, ProblemDetail problemDetail) {
        this.httpStatusCode = httpStatusCode;
        this.problemDetail = problemDetail;
    }

    public AppErrorResponse() {
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return httpStatusCode;
    }

    @Override
    public ProblemDetail getBody() {
        return problemDetail;
    }
}
