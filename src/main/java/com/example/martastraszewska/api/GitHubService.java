package com.example.martastraszewska.api;

import com.example.martastraszewska.error.GithubRepoNotFoundException;
import com.example.martastraszewska.persistence.GithubLogStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class GitHubService {
    private final RestTemplate restTemplate;
    private final GithubLogStorage githubLogStorage;
    private final ObjectMapper objectMapper;


    @Autowired
    public GitHubService(@Qualifier("githubRestTemplate") RestTemplate restTemplate, GithubLogStorage githubLogStorage, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.githubLogStorage = githubLogStorage;
        this.objectMapper = objectMapper;
    }

    public GithubReadmeResponse getRepository(String owner, String repoName) throws JsonProcessingException {
        String path = String.format("/repos/%s/%s/readme", owner, repoName);

        ResponseEntity<String> responseRaw;
        try {
            responseRaw = restTemplate.getForEntity(path, String.class);

        } catch (RestClientResponseException e) {
            if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                throw new GithubRepoNotFoundException("Repo not found");
            } else {
                throw e;
            }
        }
        System.out.println(responseRaw);
        GithubReadmeResponse response = objectMapper.readValue(responseRaw.getBody(), GithubReadmeResponse.class);
        GithubLog githubLog = new GithubLog(owner, repoName, response.getDownloadUrl());
        githubLogStorage.storeGithubLog(githubLog);
        return response;
    }
}
