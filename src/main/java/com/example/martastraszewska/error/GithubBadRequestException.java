package com.example.martastraszewska.error;

import java.util.List;

public class GithubBadRequestException extends RuntimeException {

    private List<String> parameters;

    public GithubBadRequestException(List<String> parameters) {
        super("Request does not contain required parameter");
        this.parameters = parameters;
    }

    public List<String> getParameters() {
        return parameters;
    }
}
