package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.UserNames;
import com.JobBazaar.Backend.Dto.RequestDto;
import com.JobBazaar.Backend.Dto.SignupRequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.Repositories.SnsRepository;
import com.JobBazaar.Backend.Repositories.UserRepository;
import com.JobBazaar.Backend.Utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sns.SnsClient;

import java.util.logging.Logger;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final SnsRepository snsRepository;
    private final PasswordUtils passwordUtils;

    @Autowired
    public UserService(UserRepository userRepository, SnsRepository snsRepository, PasswordUtils passwordUtils) {
        this.userRepository = userRepository;
        this.snsRepository = snsRepository;
        this.passwordUtils = passwordUtils;
    }

    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    public boolean createUser(SignupRequestDto signupRequest) {
        RequestDto requestDto = new RequestDto();
        requestDto.setEmail(signupRequest.getEmail());

        boolean userExists = userExists(requestDto);
        LOGGER.info("User created: " + !userExists);
        if (userExists) {
            LOGGER.warning("User with email: " + signupRequest.getEmail() + " already exists");
            return false;
        } else {
            //pass the item so that it added to the database
            UserDto user = new UserDto();
            user.setEmail(signupRequest.getEmail());
            user.setHashedPassword(passwordUtils.hashPassword(signupRequest.getPassword()));
            user.setFirstName(signupRequest.getFirstName());
            user.setLastName(signupRequest.getLastName());

            return userRepository.addUser(user);
        }
    }

    public boolean userExists(RequestDto request) {
        LOGGER.info("User exists request received");
        return userRepository.userExists(request);
    }

    public boolean passwordMatches(RequestDto loginRequest) {
        return userRepository.passwordMatches(loginRequest);
    }

    public boolean updateUser(RequestDto request) {
        return userRepository.updateUser(request);
    }

    public UserNames getUsersInfo(String email) {
        return userRepository.getUsersInfo(email);
    }

    public void saveTopicArn(String topic, String arn) {
        snsRepository.saveTopicArn(topic, arn);
    }

    public boolean subscriberAddedToTopic(SignupRequestDto signupRequest, String topicName) {
        return snsRepository.addSubscriberToTopic(signupRequest, topicName);
    }

    public boolean sendWelcomeMessage(String sender, String recipient, String subject, String bodyHTML) {
        return userRepository.sendWelcomeMessage(sender, recipient, subject, bodyHTML);
    }
}
