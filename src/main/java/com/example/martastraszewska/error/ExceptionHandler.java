package com.example.martastraszewska.error;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(GithubRepoNotFoundException.class)
    public ErrorResponse handleConflict() {


        return new AppErrorResponse(
                HttpStatusCode.valueOf(404),
                ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Repo not found"));

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(RestClientResponseException.class)
    public ErrorResponse handleRestClientResponseException(RestClientResponseException ex) {
        return new AppErrorResponse(
                HttpStatusCode.valueOf(500),
                ProblemDetail.forStatusAndDetail(ex.getStatusCode(), "Service is currently unavailable, please try again later."));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(GithubBadRequestException.class)
    public ErrorResponse handleBadRequestException(GithubBadRequestException ex) {
        return new AppErrorResponse(
                HttpStatusCode.valueOf(400),
                ProblemDetail.forStatusAndDetail(
                        HttpStatusCode.valueOf(400),
                        String.format(
                                "Bad request. Missing parameters: %s",
                                String.join(",", ex.getParameters())
                        )
                )
        );

    }
}
