package ru.skillbox.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.skillbox.dto.hhaccess.AreaCard;
import ru.skillbox.dto.hhaccess.CountryCard;

@FeignClient(name = "hhFeignClient", url = "${geo.base_request_url}")
public interface HhFeignClient {

  @GetMapping(value = "${geo.all_countries_request_url}")
  CountryCard[] getCountriesArray ();

  @GetMapping(value = "${geo.all_cities_request_url}" + "{countryId}")
  AreaCard getRegionsTree (@PathVariable("countryId") Long countryId);
}
