package ru.skillbox.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import ru.skillbox.entity.Dialog;
import ru.skillbox.entity.Message;
import ru.skillbox.entity.Status;
import ru.skillbox.repository.DialogRepository;
import ru.skillbox.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
abstract class AbstractTest {

    protected static PostgreSQLContainer<?> postgreSQLContainer;

    static {
        DockerImageName postgres = DockerImageName.parse("postgres:12.3");
        postgreSQLContainer = new PostgreSQLContainer<>(postgres).withReuse(true);
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.url", () -> jdbcUrl);
    }

    @Autowired
    protected DialogRepository dialogRepository;

    @Autowired
    protected MessageRepository messageRepository;

    @Autowired
    protected MockMvc mockMvc;

    protected static final String AUTHOR_UUID = "10000000-0000-0000-0000-000000000200";
    protected static final String RECIPIENT_UUID ="10000000-0000-0000-0000-000000000300";

    protected Dialog dialog = new Dialog();

    protected Message message = new Message();


    @BeforeEach
    public void setup() {

        dialog.setParticipantTwo(UUID.fromString(AUTHOR_UUID));
        dialog.setParticipantOne(UUID.fromString(RECIPIENT_UUID));
        dialog.setMessages(List.of(message));

        message.setMessageText("Hello World");
        message.setDialog(dialog);
        message.setTime(LocalDateTime.now());
        message.setStatus(Status.SENT);
        message.setAuthor(UUID.fromString(AUTHOR_UUID));
        message.setRecipient(UUID.fromString(RECIPIENT_UUID));

        dialogRepository.save(dialog);
        messageRepository.save(message);



    }

    @AfterEach
    public void afterEach() {
        dialogRepository.deleteAll();
        messageRepository.deleteAll();

    }
}
