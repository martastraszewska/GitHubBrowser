import com.example.martastraszewska.GithubApplication
import com.example.martastraszewska.api.GitHubService
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest(classes = GithubApplication,
        properties = 'application.environment=integration',
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BaseIntegrationTest extends Specification {

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected GitHubService gitHubService;

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Shared
    protected MockRestServiceServer mockServer;


    @Before
    void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build()
    }

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6")
        mongoDBContainer.start()
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer::getReplicaSetUrl("demo"));
    }
}








