package com.example.martastraszewska.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GithubReadmeResponse {
    @JsonProperty("download_url")
    private String downloadUrl;

    public GithubReadmeResponse() {
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

}
