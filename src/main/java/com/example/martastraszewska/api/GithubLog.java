package com.example.martastraszewska.api;

public class GithubLog {

    private String owner;
    private String repositoryName;
    private String readmeURL;

    public GithubLog(String owner, String repositoryName, String readmeURL) {
        this.owner = owner;
        this.repositoryName = repositoryName;
        this.readmeURL = readmeURL;
    }

    public String getOwner() {
        return owner;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getReadmeURL() {
        return readmeURL;
    }
}
