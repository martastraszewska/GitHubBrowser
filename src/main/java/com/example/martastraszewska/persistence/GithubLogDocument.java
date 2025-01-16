package com.example.martastraszewska.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("github")
public class GithubLogDocument {
    @Id
    private String id;
    private Instant timestamp;
    private String owner;
    private String repositoryName;
    private String readmeURL;

    public GithubLogDocument() {
    }

    public GithubLogDocument(String id, Instant timestamp, String owner, String repositoryName, String readmeURL) {
        this.id = id;
        this.timestamp = timestamp;
        this.owner = owner;
        this.repositoryName = repositoryName;
        this.readmeURL = readmeURL;
    }

    public String getId() {
        return id;
    }

    public Instant getTimestamp() {
        return timestamp;
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
