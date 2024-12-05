package ru.skillbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.skillbox.dto.requests.AccountRequest;
import ru.skillbox.dto.requests.LoginRequest;
import ru.skillbox.repository.AccountRepository;
import ru.skillbox.security.SecurityService;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9099", "port=9099" })
public class TestingContext {
    protected static PostgreSQLContainer postgreSQLContainer;
    protected static GenericContainer redisContainer;
    protected static String actualToken;

    static {
        DockerImageName postgres = DockerImageName.parse("postgres:12.3");
        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer(postgres)
                .withReuse(true);

        postgreSQLContainer.start();
    }

    static {
        redisContainer = new GenericContainer<>(DockerImageName.parse("mirror.gcr.io/redis")).withExposedPorts(6379)
                .withReuse(true);
        redisContainer.start();
    }

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();

        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.url", () -> jdbcUrl);
    }

    @DynamicPropertySource
    public static void registerPropertiesRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AccountRepository accountRepository;

    @BeforeEach
    public void setup() {
        securityService.register(AccountRequest.builder()
                .firstName("User")
                .lastName("Default")
                .email("Default@vk.com")
                .password1("HhAp^hSx6e421")
                .password2("HhAp^hSx6e421")
                .build());

        actualToken = securityService.login(LoginRequest.builder()
                .email("Default@vk.com")
                .password("HhAp^hSx6e421")
                .build()).getAccessToken();
    }

    @AfterEach
    public void afterEach() {
        accountRepository.deleteAll();
    }
}
