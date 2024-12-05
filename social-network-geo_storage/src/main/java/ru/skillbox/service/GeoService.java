package ru.skillbox.service;

import ru.skillbox.dto.CityDto;
import ru.skillbox.dto.CountryDto;

import java.util.List;

public interface GeoService {
  List<CountryDto> getCountries();

  List<CityDto> getCities(Long countryId);
}
