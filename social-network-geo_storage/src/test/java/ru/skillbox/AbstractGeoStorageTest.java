package ru.skillbox;

import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.skillbox.entity.ImageRecord;
import ru.skillbox.repository.ImageRepository;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource("classpath:/application-test.yml")
@Sql("classpath:db/init.sql")
@Transactional
public abstract class AbstractGeoStorageTest {
  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  protected ObjectMapper objectMapper;

  protected static List<S3ObjectSummary> summ = new ArrayList<>();
  protected ObjectListing listing = Mockito.mock(ObjectListing.class);
  protected DeleteObjectsResult delResult = Mockito.mock(DeleteObjectsResult.class);


  @Container
  protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
        new PostgreSQLContainer<>("postgres:16.2-alpine")
              .withDatabaseName("social-network-images")
              .withReuse(true)
              .withUsername("postgres")
              .withPassword("postgres")
              .withExposedPorts(5432);

  @DynamicPropertySource
  public static void registerProperties (DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
  }

  public static void prepareMockCloud (ImageRepository repository, int indent) {
    List<ImageRecord> allInDb = repository.findAll();
    for (ImageRecord imageRecord : allInDb) {
      S3ObjectSummary item = new S3ObjectSummary();
      item.setKey(imageRecord.getValue().substring(indent));
      item.setSize(2000L);
      summ.add(item);
    }
  }

}
