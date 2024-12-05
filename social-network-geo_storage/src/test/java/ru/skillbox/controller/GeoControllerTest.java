package ru.skillbox.controller;

import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GeoControllerTest extends AbstractGeoTest {

  @Test
  @WithMockUser
  void whenGetCountries_thenReturnCountries () throws Exception {
    assertTrue(redisTemplate.keys("*").isEmpty());
    String receivedResponse = mockMvc.perform(get("/api/v1/geo/country"))
          .andExpect(status().isOk())
          .andReturn()
          .getResponse()
          .getContentAsString();

    assertFalse(redisTemplate.keys("*").isEmpty());
    JsonAssert.assertJsonEquals(objectMapper.writeValueAsString(expected), receivedResponse);
  }

  @Test
  @WithMockUser
  void whenGetCitiesOfCountryWithCities_thenReturnCities() throws Exception {
    assertTrue(redisTemplate.keys("*").isEmpty());
    String receivedResponse = mockMvc.perform(get("/api/v1/geo/country/1/city"))
          .andExpect(status().isOk())
          .andReturn()
          .getResponse()
          .getContentAsString();

    assertFalse(redisTemplate.keys("*").isEmpty());
    JsonAssert.assertJsonEquals(objectMapper.writeValueAsString(citiesOfCountry1), receivedResponse);
  }

  @Test
  @WithMockUser
  void whenGetCitiesOfCountryWithoutCities_thenReturnOneCity() throws Exception {
    assertTrue(redisTemplate.keys("*").isEmpty());
    String receivedResponse = mockMvc.perform(get("/api/v1/geo/country/2/city"))
          .andExpect(status().isOk())
          .andReturn()
          .getResponse()
          .getContentAsString();
    assertFalse(redisTemplate.keys("*").isEmpty());
    JsonAssert.assertJsonEquals(objectMapper.writeValueAsString(citiesOfCountry2), receivedResponse);
  }

  @Test
  @WithMockUser
  void whenGetCitiesOfCountryThatDoesNotExist_thenReturnError () throws Exception {
    String receivedResponse = mockMvc.perform(get("/api/v1/geo/country/3/city"))
          .andExpect(status().is5xxServerError())
          .andReturn()
          .getResponse()
          .getContentAsString();
    assertTrue(receivedResponse.contains("Can`t retrieve cities list"));
  }

}