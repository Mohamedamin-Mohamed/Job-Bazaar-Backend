package com.example.Backend.Services;

import com.example.Backend.Utils.PasswordUtils;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private DynamoDbClient client;
    String tableName = "Users";

    public UserService(){
        this.client = DynamoDbClient.builder().region(Region.US_EAST_2).build();
    }
    public boolean createUser(String email, String plainTextPassword){
        //check whether there is a user that exists with the above credentials
        boolean userExists = userExists(email);
        if(userExists){
            return false;
        }
        //create the user because they don't exist
        else{
           String hashedPassword = PasswordUtils.hashPassword(plainTextPassword);
           Map<String, AttributeValue> item = new HashMap<>();

           item.put("email", AttributeValue.builder().s(email).build());
           item.put("hashedPassword", AttributeValue.builder().s(hashedPassword).build());

            PutItemRequest req = PutItemRequest.builder().tableName(tableName).item(item).build();
            try {
                //put the item into the table
                PutItemResponse resp = client.putItem(req);
                return true;
            }
            catch(DynamoDbException exp){
                System.out.println("Something went wrong when inserting user into the table " + exp.statusCode() + exp.getMessage());
            }
        }
        //if we reach here it means an exception was thrown when inserting the user into the table
        return false;
    }
    public boolean userExists(String email){
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(email).build());
        GetItemRequest req = GetItemRequest.builder().tableName(tableName).key(key).build();
        try {
            //it return an object which can be checked if its null and if its empty
            GetItemResponse resp = client.getItem(req);
            Map<String, AttributeValue> item = resp.item();
            return !item.isEmpty() && item != null;
        }
        catch(DynamoDbException exp){
            System.out.println("Exception " + exp.statusCode());
        }
        return false;
    }
}
