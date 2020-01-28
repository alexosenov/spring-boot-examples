package bg.osenov.mappers;

import bg.osenov.dto.avro.AvroPersonDto;
import bg.osenov.models.Person;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-01-28T16:02:21+0200",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_112 (Oracle Corporation)"
)
@Component
public class PersonMapperImpl implements PersonMapper {

    @Override
    public Person avroPersonDtoToPersonEntity(AvroPersonDto dto) {
        if ( dto == null ) {
            return null;
        }

        Person person = new Person();

        return person;
    }
}
