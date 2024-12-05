package ru.skillbox.service.impl;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.skillbox.configuration.CacheNames;
import ru.skillbox.dto.CityDto;
import ru.skillbox.dto.CountryDto;
import ru.skillbox.dto.hhaccess.AreaCard;
import ru.skillbox.dto.hhaccess.CountryCard;
import ru.skillbox.exception.AccessResourseException;
import ru.skillbox.httpclient.HhFeignClient;
import ru.skillbox.mapper.GeoMapperStruct;
import ru.skillbox.service.GeoService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "redisCacheManager")
public class GeoServiceImpl implements GeoService {
  private final HhFeignClient feignClient;
  private final GeoMapperStruct geoMapper;

  @Override
  @Cacheable(cacheNames = CacheNames.COUNTRIES_CACHE_NAME)
  public List<CountryDto> getCountries() {
    CountryCard[] hhAnswer;
    try {
      hhAnswer = feignClient.getCountriesArray();
    } catch (FeignException ex) {
      throw new AccessResourseException(MessageFormat.format("Can`t retrieve countries list from Head Hunter! {0}",
            ex.getLocalizedMessage()));
    }
    List<CountryDto> result = geoMapper.allCountriesCardsToDto(hhAnswer);
    for (CountryDto country : result) {
      country.setCities(getCities(country.getId()));
    }
    return result;
  }

  @Override
  @Cacheable(cacheNames = CacheNames.CITIES_CACHE_NAME)
  public List<CityDto> getCities(Long countryId) {
    AreaCard hhAnswer;
    try {
      hhAnswer = feignClient.getRegionsTree(countryId);
    } catch (FeignException ex) {
      throw new AccessResourseException(MessageFormat.format(
            "Can`t retrieve cities list from Head Hunter for countryId {0}! {1}",
            countryId, ex.getLocalizedMessage()));
    }
    List<AreaCard> areas = getAllMembers(List.of(hhAnswer), new ArrayList<>());
    areas = areas.stream()
          .filter(ac -> ac.getAreas().length == 0)
          .toList();
    return geoMapper.allAreasToCitiesDto(areas, countryId);
  }

  private List<AreaCard> getAllMembers(List<AreaCard> sourceCards, List<AreaCard> resultList) {
    if (sourceCards.isEmpty()) {
      return resultList;
    }
    List<AreaCard> nextStepCards = new ArrayList<>();
    for (AreaCard card : sourceCards) {
      if (card != null) {
        resultList.add(card);
        nextStepCards.addAll(List.of(card.getAreas()));
      }
    }
    return getAllMembers(nextStepCards, resultList);
  }

}
