package com.example.martastraszewska.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GithubLogRepository extends MongoRepository<GithubLogDocument, String> {

   List<GithubLogDocument> findByOwnerAndRepositoryName (String owner, String repoName);


}
