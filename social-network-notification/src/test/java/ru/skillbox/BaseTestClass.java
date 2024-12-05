package ru.skillbox;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.skillbox.repository.NotificationRepository;
import ru.skillbox.repository.NotificationSettingRepository;
import ru.skillbox.repository.NotificationToUserRepository;
import ru.skillbox.service.impl.NotificationServiceImp;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@ExtendWith(SpringExtension.class)
@Transactional
public class BaseTestClass {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected NotificationSettingRepository notificationSettingRepository;
    @Autowired
    protected NotificationRepository notificationRepository;
    @Autowired
    protected NotificationToUserRepository notificationToUserRepository;
    @Autowired
    protected NotificationServiceImp notificationServiceImp;

    @PersistenceContext
    private EntityManager entityManager;

    protected static PostgreSQLContainer postgreSQLContainer;

    static {
        DockerImageName postgres = DockerImageName.parse("postgres:12.3");
        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer(postgres)
                .withReuse(true);

        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();

        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.url", () -> jdbcUrl);
    }


    @AfterEach
    public void afterEach() {
        notificationToUserRepository.deleteAll();
        notificationRepository.deleteAll();
        notificationSettingRepository.deleteAll();
    }

}
