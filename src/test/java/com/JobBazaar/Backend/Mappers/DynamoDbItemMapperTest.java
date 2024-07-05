package com.JobBazaar.Backend.Mappers;

import com.JobBazaar.Backend.Dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DynamoDbItemMapperTest {
    @InjectMocks
    DynamoDbItemMapper dynamoDbItemMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toDynamoDbItemMaps(){
        UserDto userDto = new UserDto();
        userDto.setHashedPassword("hashedPassword");
        userDto.setFirstName("test");
        userDto.setLastName("com");
        userDto.setEmail("test@test.com");

        Map<String, AttributeValue> expected = dynamoDbItemMapper.toDynamoDbItemMap(userDto);

        assertNotNull(expected);
        assertEquals(expected.size(), 4);
        assertEquals("test@test.com", expected.get("email").s());
    }
}