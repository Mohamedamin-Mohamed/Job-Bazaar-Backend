package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.UserNames;
import com.JobBazaar.Backend.Dto.RequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import com.JobBazaar.Backend.Utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SendEmailResponse;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Repository
public class UserRepository {

    private final DynamoDbClient client;
    private final DynamoDbItemMapper itemMapper;
    private final PasswordUtils passwordUtils;
    private final SesV2Client sesV2Client;

    private final String USERS = "Users";
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    @Autowired
    public UserRepository(DynamoDbClient client, DynamoDbItemMapper itemMapper, PasswordUtils passwordUtils, SesV2Client sesV2Client) {
        this.client = client;
        this.itemMapper = itemMapper;
        this.passwordUtils = passwordUtils;
        this.sesV2Client = sesV2Client;
    }

    public boolean addUser(UserDto user) {
        LOGGER.info("Adding user: " + user.toString());
        Map<String, AttributeValue> item = itemMapper.toDynamoDbItemMap(user);
        PutItemRequest request = PutItemRequest.builder().tableName(USERS).item(item).build();
        try {
            PutItemResponse response = client.putItem(request);
            LOGGER.info("Created user with email " + user.getEmail());
            return response.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.warning("User account couldn't be created " + exp);
            throw exp;
        }
    }

    public boolean updateUser(RequestDto requestDto) {
        LOGGER.info("Updating user with email " + requestDto.getEmail());
        //hash the users password
        String hashedPassword = passwordUtils.hashPassword(requestDto.getPassword());

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(requestDto.getEmail()).build());
        key.put("hashedPassword", AttributeValue.builder().s(hashedPassword).build());
        PutItemRequest req = PutItemRequest.builder().tableName(USERS).item(key).build();
        try {
            PutItemResponse resp = client.putItem(req);
            LOGGER.info("Changed password for user with email " + requestDto.getEmail());
            return true;
        } catch (DynamoDbException exp) {
            LOGGER.warning(exp.toString());
            throw exp;
        }
    }

    public boolean passwordMatches(RequestDto requestDto) {
        LOGGER.info("Checking password for user with email " + requestDto.getEmail());

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(requestDto.getEmail()).build());
        String hashedPassword;

        GetItemRequest request = GetItemRequest.builder().tableName(USERS).key(key).build();
        try {
            GetItemResponse response = client.getItem(request);
            Map<String, AttributeValue> item = response.item();
            if (item != null && !item.isEmpty()) {
                AttributeValue hashedPasswordAttr = item.get("hashedPassword");
                hashedPassword = hashedPasswordAttr.s();
                LOGGER.info("Comparing plainText password from the user with the stored hashed password");

                //now compare the hashedPassword retrieved with the plainText from the user
                return passwordUtils.checkPassword(requestDto.getPassword(), hashedPassword);
            } else return false;
        } catch (DynamoDbException exp) {
            LOGGER.warning(exp.toString());
            throw exp;
        }
    }

    public boolean userExists(RequestDto requestDto) {
        LOGGER.info("Checking if user with email " + requestDto.getEmail() + " exists");
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(requestDto.getEmail()).build());
        GetItemRequest req = GetItemRequest.builder().tableName(USERS).key(key).build();
        try {
            //it returns an object which can be checked if its null and if its empty
            GetItemResponse resp = client.getItem(req);
            Map<String, AttributeValue> item = resp.item();
            //check if the item exists and is not empty
            return item != null && !item.isEmpty();
        } catch (DynamoDbException exp) {
            LOGGER.warning(exp.toString());
            throw exp;
        }
    }

    public UserNames getUsersInfo(String email) {
        LOGGER.info("Grabbing " + email + " info");
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(email).build());
        GetItemRequest req = GetItemRequest.builder().tableName(USERS).key(key).build();
        try {
            //it returns an object which can be checked if its null and if its empty
            GetItemResponse resp = client.getItem(req);
            Map<String, AttributeValue> item = resp.item();
            UserNames person = null;
            if (item != null && !item.isEmpty()) {
                person = new UserNames();
                System.out.println("Name is " + item.get("firstName").s());
                String firstName = item.get("firstName").s();
                String lastName = item.get("lastName").s();

                person.setFirstName(firstName);
                person.setLastName(lastName);
            }
            return person;
        } catch (DynamoDbException exp) {
            LOGGER.warning(exp.toString());
            throw exp;
        }
    }

    public boolean sendWelcomeMessage(String sender, String recipient, String subject, String bodyHTML) {
        Destination destination = Destination.builder().toAddresses(recipient).build();
        Content content = Content.builder().data(bodyHTML).build();
        Content sub = Content.builder().data(subject).build();
        Body body = Body.builder().html(content).build();
        Message msg = Message.builder().subject(sub).body(body).build();
        EmailContent emailContent = EmailContent.builder().simple(msg).build();

        SendEmailRequest emailRequest = SendEmailRequest.builder().destination(destination).fromEmailAddress(sender).content(emailContent).build();
        try {
            SendEmailResponse sendEmailResponse = sesV2Client.sendEmail(emailRequest);
            return sendEmailResponse.sdkHttpResponse().isSuccessful();
        } catch (SesV2Exception exp) {
            LOGGER.warning(exp.toString());
            throw exp;
        }
    }

}
