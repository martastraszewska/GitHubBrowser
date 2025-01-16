package com.example.martastraszewska.api;

import com.example.martastraszewska.error.GithubBadRequestException;
import com.example.martastraszewska.persistence.GithubLogDocument;
import com.example.martastraszewska.persistence.GithubLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GitHubController {
    @Autowired
    private GitHubService gitHubService;
    @Autowired
    private GithubLogRepository githubLogRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/repository")
    public GithubReadmeResponse getRepository(@RequestParam(required = false) String repoName,
                                              @RequestParam(required = false) String owner) throws JsonProcessingException {
        validateParameters(repoName, owner);
        return gitHubService.getRepository(owner, repoName);

    }

    private void validateParameters(String repoName, String owner) {
        List<String> invalidParameters = new ArrayList<>();
        if (StringUtils.isEmpty(owner)) {
            invalidParameters.add("owner");
        }
        if (StringUtils.isEmpty(repoName)) {
            invalidParameters.add("repoName");
        }
        if (!invalidParameters.isEmpty()) {
            throw new GithubBadRequestException(invalidParameters);
        }
    }

    @GetMapping("/db")
    public ResponseEntity<List<GithubLogDocument>> publishMessage(@RequestParam String owner, @RequestParam String repoName) {
        return ResponseEntity.ok(githubLogRepository.findByOwnerAndRepositoryName(owner, repoName));
    }
}

