package ru.skillbox.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.dto.ImageRecordDto;
import ru.skillbox.entity.ImageRecord;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageRecordMapper {
  ImageRecord dtoToEntity (ImageRecordDto dto);
}
