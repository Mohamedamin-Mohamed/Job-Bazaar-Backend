package com.example.Backend.Mappers;

import com.example.Backend.Dto.UserDto;
import org.springframework.web.bind.annotation.Mapping;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class DynamoDbItemMapper {
    public Map<String, AttributeValue> toDynamoDbItemMap(UserDto user) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("email", AttributeValue.builder().s(user.getEmail()).build());
        item.put("hashedPassword", AttributeValue.builder().s(user.getHashedPassword()).build());

        return item;
    }
}
