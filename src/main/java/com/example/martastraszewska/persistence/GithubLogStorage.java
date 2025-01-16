package com.example.martastraszewska.persistence;

import com.example.martastraszewska.api.GithubLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component

public class GithubLogStorage {
    @Autowired
    private GithubLogRepository githubLogRepository;

    public void storeGithubLog(GithubLog githubLog) {
        GithubLogDocument document = new GithubLogDocument(UUID.randomUUID().toString(), Instant.now(), githubLog.getOwner(), githubLog.getRepositoryName(), githubLog.getReadmeURL());
        githubLogRepository.save(document);
    }

}
