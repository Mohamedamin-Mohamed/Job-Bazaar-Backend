package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.RequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import com.JobBazaar.Backend.Utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Repository
public class UserRepository {

    private final DynamoDbClient client;
    private final DynamoDbItemMapper itemMapper;
    private final PasswordUtils passwordUtils;

    private final String TABLE_NAME = "Users";
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    @Autowired
    public UserRepository(DynamoDbClient client, DynamoDbItemMapper itemMapper, PasswordUtils passwordUtils) {
        this.client = client;
        this.itemMapper = itemMapper;
        this.passwordUtils = passwordUtils;
    }

    public boolean addUser(UserDto user) {
        LOGGER.info("Adding user: " + user.toString());
        Map<String, AttributeValue> item = itemMapper.toDynamoDbItemMap(user);
        PutItemRequest request = PutItemRequest.builder().tableName(TABLE_NAME).item(item).build();
        try{
            PutItemResponse response = client.putItem(request);
            LOGGER.info("Created user with email " + user.getEmail());
            return true;
        }
        catch (DynamoDbException e) {
            LOGGER.warning("User account couldn't be created " + e.toString());
        }

        //if we reach here it means an exception was thrown when inserting the user into the table
        return false;
    }
    public boolean updateUser(RequestDto requestDto) {
        LOGGER.info("Updating user with email " + requestDto.getEmail());
        //hash the users password
        String hashedPassword = passwordUtils.hashPassword(requestDto.getPassword());

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(requestDto.getEmail()).build());
        key.put("hashedPassword", AttributeValue.builder().s(hashedPassword).build());
        PutItemRequest req = PutItemRequest.builder().tableName(TABLE_NAME).item(key).build();
        try{
            PutItemResponse resp = client.putItem(req);
            LOGGER.info("Changed password for user with email " + requestDto.getEmail());
            return true;
        }
        catch(DynamoDbException exp){
            LOGGER.warning(exp.toString());
        }
        return false;
    }
    public boolean passwordMatches(RequestDto requestDto) {
        LOGGER.info("Checking password for user with email " + requestDto.getEmail());

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(requestDto.getEmail()).build());
        String hashedPassword;

        GetItemRequest request = GetItemRequest.builder().tableName(TABLE_NAME).key(key).build();
        try{
            GetItemResponse response = client.getItem(request);
            Map<String, AttributeValue> item = response.item();
            if(item != null && !item.isEmpty()){
                AttributeValue hashedPasswordAttr = item.get("hashedPassword");
                hashedPassword = hashedPasswordAttr.s();
                LOGGER.info("Comparing plainText password from the user with the stored hashed password");

                //now compare the hashedPassword retrieved with the plainText from the user
                return passwordUtils.checkPassword(requestDto.getPassword(), hashedPassword);
            }
        }
        catch (DynamoDbException exp){
            LOGGER.warning(exp.toString());
        }
        return false;
    }
    public boolean userExists(RequestDto requestDto) {
        LOGGER.info("Checking if user with email " + requestDto.getEmail() + " exists");
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(requestDto.getEmail()).build());
        GetItemRequest req = GetItemRequest.builder().tableName(TABLE_NAME).key(key).build();
        try {
            //it returns an object which can be checked if its null and if its empty
            GetItemResponse resp = client.getItem(req);
            Map<String, AttributeValue> item = resp.item();
            //check if the item exists and is not empty
            return item != null && !item.isEmpty();
        }
        catch(DynamoDbException exp){
            LOGGER.warning(exp.toString());
        }
        return false;
    }

}
