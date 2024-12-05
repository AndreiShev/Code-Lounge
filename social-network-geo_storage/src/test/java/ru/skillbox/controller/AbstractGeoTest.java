package ru.skillbox.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import ru.skillbox.AbstractGeoStorageTest;
import ru.skillbox.dto.CityDto;
import ru.skillbox.dto.CountryDto;
import ru.skillbox.dto.hhaccess.AreaCard;
import ru.skillbox.dto.hhaccess.CountryCard;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Transactional
public abstract class AbstractGeoTest extends AbstractGeoStorageTest {
  @Autowired
  protected RedisTemplate<String, Object> redisTemplate;

  public static List<CityDto> citiesOfCountry1 = List.of(
        new CityDto(3L, "City1", 1L), new CityDto(4L, "City2", 1L));
  public static List<CityDto> citiesOfCountry2 = List.of(
        new CityDto(2L, "Country2", 2L));
  public static List<CountryDto> expected = List.of(new CountryDto(1L, "Country1", citiesOfCountry1),
        new CountryDto(2L, "Country2", citiesOfCountry2));

  @RegisterExtension
  protected static WireMockExtension wireMockServer = WireMockExtension.newInstance()
        .options(wireMockConfig().dynamicPort())
        .build();

  @Container
  protected static final RedisContainer REDIS_CONTAINER = new RedisContainer(
        DockerImageName.parse("mirror.gcr.io/redis"))
        .withExposedPorts(6379)
        .withReuse(true);

  @DynamicPropertySource
  public static void registerProperties (DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    registry.add("spring.data.redis.port",
          () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    registry.add("geo.base_request_url", () -> wireMockServer.baseUrl() + "/areas");
    registry.add("all_countries_request_url", () -> "/countries");
    registry.add("all_cities_request_url", () -> "/");
  }

  @BeforeEach
  public void beforeEach () throws JsonProcessingException {
    redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    stubClients();
  }

  @AfterEach
  public void afterEach () {
    wireMockServer.resetAll();
  }

  private void stubClients () throws JsonProcessingException {
    List<CountryCard> countryCards = new ArrayList<>();
    countryCards.add(new CountryCard("1", "Country1", ""));
    countryCards.add(new CountryCard("2", "Country2", ""));
    wireMockServer.stubFor(WireMock.get("/areas/countries")
          .willReturn(aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsString(countryCards))
                .withStatus(200)));

    AreaCard areaCardCity1 = new AreaCard("3", "1", "City1", new AreaCard[0]);
    AreaCard areaCardCity2 = new AreaCard("4", "1", "City2", new AreaCard[0]);
    AreaCard[] citiesOfCountry1Array = {areaCardCity1, areaCardCity2};
    AreaCard areasCountry1 = new AreaCard("1", "", "Country1", citiesOfCountry1Array);
    wireMockServer.stubFor(WireMock.get("/areas/1")
          .willReturn(aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsString(areasCountry1))
                .withStatus(200)));

    AreaCard areasCountry2 = new AreaCard("2", "", "Country2", new AreaCard[0]);
    wireMockServer.stubFor(WireMock.get("/areas/2")
          .willReturn(aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsString(areasCountry2))
                .withStatus(200)));

    wireMockServer.stubFor(WireMock.get("/areas/3")
          .willReturn(aResponse()
                .withStatus(404)));
  }
}
