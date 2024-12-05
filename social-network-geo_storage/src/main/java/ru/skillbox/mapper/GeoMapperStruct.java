package ru.skillbox.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.dto.CityDto;
import ru.skillbox.dto.CountryDto;
import ru.skillbox.dto.hhaccess.AreaCard;
import ru.skillbox.dto.hhaccess.CountryCard;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GeoMapperStruct {

  @Mapping(target = "title", source = "name")
  CountryDto countryCardToDto (CountryCard card);

  @Mapping(target = "title", source = "areaCard.name")
  @Mapping(target = "countryId", expression = "java(countryId)")
  CityDto areaCardToCityDto (AreaCard areaCard, Long countryId);

  default List<CountryDto> allCountriesCardsToDto (CountryCard[] allCountriesCard) {
    return Arrays.stream(allCountriesCard)
          .map(this::countryCardToDto)
          .toList();
  }

  default List<CityDto> allAreasToCitiesDto(List<AreaCard> areas, Long countryId) {
    return areas.stream()
          .map(ac -> areaCardToCityDto(ac, countryId))
          .toList();
  }
}
