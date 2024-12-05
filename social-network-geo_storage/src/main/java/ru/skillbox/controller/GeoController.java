package ru.skillbox.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.dto.CityDto;
import ru.skillbox.dto.CountryDto;
import ru.skillbox.service.GeoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/geo")
@RequiredArgsConstructor
public class GeoController {
  private final GeoService geoService;

  @GetMapping("/country")
  @ResponseStatus(value = HttpStatus.OK)
  public List<CountryDto> getCountries () {
    return geoService.getCountries();
  }

  @GetMapping("/country/{countryId}/city")
  @ResponseStatus(value = HttpStatus.OK)
  public List<CityDto> getCities (@PathVariable Long countryId) {
    return geoService.getCities(countryId);
  }

}
