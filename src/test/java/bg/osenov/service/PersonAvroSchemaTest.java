package bg.osenov.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.avro.Schema;
import org.apache.avro.SchemaCompatibility;
import org.apache.avro.SchemaValidationException;
import org.apache.avro.SchemaValidator;
import org.apache.avro.SchemaValidatorBuilder;
import org.junit.Test;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityChecker;

public class PersonAvroSchemaTest {

	@Test
	public void test_AvroPersonDto_Add_New_Not_Null_Field() throws IOException{
		//Adding new not null field should cause an error
		
        File personAvscFile = new File("src/main/resources/avro_schemas/Person.avsc").getAbsoluteFile();
        Schema personAvroSchemaOld = new Schema.Parser().parse(personAvscFile);
        
        // added middlename field not null
        String newSchemaString = "{\"namespace\": \"bg.osenov.dto.avro\",\r\n  \"type\": \"record\",\r\n  \"name\": \"AvroPersonDto\",\r\n  \"fields\" : [\r\n    {\"name\": \"firstname\", \"type\": \"string\"},\r\n    {\"name\": \"lastname\", \"type\": \"string\"},\r\n\t{\"name\": \"middlename\", \"type\": \"string\"},\r\n    {\"name\": \"email\", \"type\": \"string\"}\r\n  ]\r\n}";
        Schema newSchemaToTest = new Schema.Parser().parse(newSchemaString);
        
        //Check Schema Registry compatibility
        assertFalse(AvroCompatibilityChecker.BACKWARD_CHECKER.isCompatible(newSchemaToTest, personAvroSchemaOld));
        
        SchemaValidator backwardValidator = new SchemaValidatorBuilder().canReadStrategy().validateLatest();
        assertThatThrownBy(()-> backwardValidator.validate(newSchemaToTest, Collections.singletonList(personAvroSchemaOld)))
        	.isInstanceOf(SchemaValidationException.class);
       
        
        //Recursively check properties TYPE ( ex: string field has not become int)
        //assuming that writer schema is with newer version than reader's
        SchemaCompatibility.SchemaPairCompatibility compatResult = SchemaCompatibility.checkReaderWriterCompatibility(personAvroSchemaOld,newSchemaToTest);
        assertTrue(SchemaCompatibility.schemaNameEquals(personAvroSchemaOld,newSchemaToTest));
        assertNotNull(compatResult);
        assertEquals(SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE, compatResult.getType());

	}
	
	
	@Test
	public void test_AvroPersonDto_Add_New_Nullable_Field() throws IOException, SchemaValidationException {

		File personAvscFile = new File("src/main/resources/avro_schemas/Person.avsc").getAbsoluteFile();
		Schema personAvroSchemaOld = new Schema.Parser().parse(personAvscFile);

		// added middlename field nullable
		String newSchemaString = "{\r\n  \"namespace\": \"bg.osenov.dto.avro\",\r\n  \"type\": \"record\",\r\n  \"name\": \"AvroPersonDto\",\r\n  \"fields\" : [\r\n    {\"name\": \"firstname\", \"type\": \"string\"},\r\n    {\"name\": \"lastname\", \"type\": \"string\"},\r\n\t{\"name\": \"middlename\", \"type\": [\"null\",\"string\"], \"default\":null},\r\n    {\"name\": \"email\", \"type\": \"string\"}\r\n  ]\r\n}";
		Schema newSchemaToTest = new Schema.Parser().parse(newSchemaString);

		// Check Schema Registry compatibility
		assertTrue(AvroCompatibilityChecker.BACKWARD_CHECKER.isCompatible(newSchemaToTest, personAvroSchemaOld));

		SchemaValidator backwardValidator = new SchemaValidatorBuilder().canReadStrategy().validateLatest();
		backwardValidator.validate(newSchemaToTest, Collections.singletonList(personAvroSchemaOld));

		SchemaCompatibility.SchemaPairCompatibility compatResult = SchemaCompatibility
				.checkReaderWriterCompatibility(personAvroSchemaOld, newSchemaToTest);
		assertTrue(SchemaCompatibility.schemaNameEquals(personAvroSchemaOld, newSchemaToTest));
		assertNotNull(compatResult);
		assertEquals(SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE, compatResult.getType());
	}
}
