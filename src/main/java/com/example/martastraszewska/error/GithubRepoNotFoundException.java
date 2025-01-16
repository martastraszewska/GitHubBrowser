package com.example.martastraszewska.error;

public class GithubRepoNotFoundException extends RuntimeException{

    public GithubRepoNotFoundException(String message) {
        super(message);
    }
}
