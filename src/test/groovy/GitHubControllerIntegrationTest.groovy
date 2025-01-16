import com.example.martastraszewska.api.GithubReadmeResponse
import com.example.martastraszewska.persistence.GithubLogDocument
import com.example.martastraszewska.persistence.GithubLogRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.ResponseActions
import org.springframework.test.web.client.match.MockRestRequestMatchers
import spock.lang.Unroll


import java.time.Instant

import static org.hamcrest.Matchers.is
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus


class GitHubControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private GithubLogRepository githubLogRepository;


    void setup() {
        githubLogRepository.deleteAll()
    }


    def 'should return readme url from github'() {

        given:
        mockServer.expect(requestTo(is('https://api.github.com/repos/martastraszewska/CustomerManagement/readme')))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andExpect(MockRestRequestMatchers.header("Accept", "application/vnd.github+json"))
                .andExpect(MockRestRequestMatchers.header("X-GitHub-Api-Version", "2022-11-28"))
                .andRespond(withStatus(HttpStatusCode.valueOf(200)).contentType(MediaType.APPLICATION_JSON)
                        .body(getClass().getResourceAsStream('/json/git_hub_response.json').text))
        when:
        def responseEntity = testRestTemplate.getForEntity("/repository?repoName=CustomerManagement&owner=martastraszewska", GithubReadmeResponse.class)

        then:
        responseEntity.statusCode == HttpStatusCode.valueOf(200)
        responseEntity.body.downloadUrl == "https://raw.githubusercontent.com/martastraszewska/CustomerManagement/master/README.md"

    }

    def 'should return GithubRepoNotFoundException if repo not found'() {

        given:
        mockServer.expect(requestTo(is('https://api.github.com/repos/owner/repoName/readme')))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andExpect(MockRestRequestMatchers.header("Accept", "application/vnd.github+json"))
                .andExpect(MockRestRequestMatchers.header("X-GitHub-Api-Version", "2022-11-28"))
                .andRespond(withStatus(HttpStatusCode.valueOf(404)))

        when:
        def responseEntity = testRestTemplate.getForEntity('/repository?repoName=repoName&owner=owner', Map.class)
        then:
        responseEntity.statusCodeValue == 404
        responseEntity.body.get("detail") == "Repo not found"
    }

    def 'should save readme url from github in db'() {

        given:
        mockServer.expect(requestTo(is('https://api.github.com/repos/martastraszewska/CustomerManagement/readme')))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andExpect(MockRestRequestMatchers.header("Accept", "application/vnd.github+json"))
                .andExpect(MockRestRequestMatchers.header("X-GitHub-Api-Version", "2022-11-28"))
                .andRespond(withStatus(HttpStatusCode.valueOf(200)).contentType(MediaType.APPLICATION_JSON)
                        .body(getClass().getResourceAsStream('/json/git_hub_response.json').text))
        when:
        def responseEntity = testRestTemplate.getForEntity("/repository?repoName=CustomerManagement&owner=martastraszewska", GithubReadmeResponse.class)

        then:
        githubLogRepository.count() == 1
        GithubLogDocument logDocument = githubLogRepository.findAll().first()
        logDocument.readmeURL == responseEntity.body.downloadUrl
    }

    def 'should not save download url when GithubRepoNotFoundException was thrown'() {

        given:
        mockServer.expect(requestTo(is('https://api.github.com/repos/blabla/blabla/readme')))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andExpect(MockRestRequestMatchers.header("Accept", "application/vnd.github+json"))
                .andExpect(MockRestRequestMatchers.header("X-GitHub-Api-Version", "2022-11-28"))
                .andRespond(withStatus(HttpStatusCode.valueOf(404)))

        when:
        def responseEntity = testRestTemplate.getForEntity("/repository?repoName=blabla&owner=blabla", Map.class)
        then:
        responseEntity.statusCodeValue == 404
        githubLogRepository.count() == 0

    }

    @Unroll
    def 'should return bad request on missing query params'() {

        given:
        noneRequestsToGithub()

        when:
        def responseEntity = testRestTemplate.getForEntity(path, Map.class)


        then:
        responseEntity.body.get("detail") == "Bad request. Missing parameters: $missingParams"
        responseEntity.getStatusCode().value() == 400

        and:
        mockServer.verify()

        where:
        path                            || missingParams
        "/repository?repoName=repoName" || "owner"
        "/repository?owner=owner"       || "repoName"
        "/repository"                   || "owner,repoName"
    }

    private ResponseActions noneRequestsToGithub() {
        mockServer.expect(ExpectedCount.never(), MockRestRequestMatchers.anything())
    }

    def 'should return RestClientResponseException if internal server error'() {

        given:
        returnAlways500FromGithub()

        when:
        def responseEntity = testRestTemplate.getForEntity("/repository?repoName=blabla&owner=blabla", Map.class)
        then:
        responseEntity.statusCodeValue == 500
        responseEntity.body.get("detail") == "Service is currently unavailable, please try again later."
    }

    private returnAlways500FromGithub() {
        mockServer.expect(MockRestRequestMatchers.anything())
                .andRespond(withStatus(HttpStatusCode.valueOf(500)).body())
    }

    def 'should return GithubLogDocument from db'() {
        given:
        GithubLogDocument document = new GithubLogDocument(UUID.randomUUID().toString(), Instant.now(), "owner", "repositoryName", "dummy");
        githubLogRepository.save(document);


        when:
        def responseEntity = testRestTemplate.getForEntity("/db?repoName=repositoryName&owner=owner", List<GithubLogDocument>.class)

        then:
        responseEntity.statusCode == HttpStatusCode.valueOf(200)
        def responseDocuments = responseEntity.body
        assert responseDocuments.size() == 1
        def responseDocument = responseDocuments[0]

        document.readmeURL == responseDocument.readmeURL
        document.id == responseDocument.id
        document.owner == responseDocument.owner
        document.repositoryName == responseDocument.repositoryName
    }


}

