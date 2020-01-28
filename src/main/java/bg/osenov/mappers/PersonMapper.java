package bg.osenov.mappers;

import org.mapstruct.Mapper;

import bg.osenov.dto.avro.AvroPersonDto;
import bg.osenov.models.Person;

@Mapper(componentModel = "spring")
public interface PersonMapper {

	Person avroPersonDtoToPersonEntity(AvroPersonDto dto);
}
