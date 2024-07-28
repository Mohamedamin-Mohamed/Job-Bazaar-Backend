package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.AppUser;
import com.JobBazaar.Backend.Dto.PasswordResetDto;
import com.JobBazaar.Backend.Dto.RequestDto;
import com.JobBazaar.Backend.Dto.SignupRequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.Repositories.SnsRepository;
import com.JobBazaar.Backend.Repositories.UserRepository;
import com.JobBazaar.Backend.Utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.logging.Logger;

@Service
public class UserService implements UserDetailsService {
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

    public UserDto createUser(SignupRequestDto signupRequest) {
        RequestDto requestDto = new RequestDto();
        requestDto.setEmail(signupRequest.getEmail());

        boolean userExists = userExists(requestDto);
        LOGGER.info("User created: " + !userExists);
        if (userExists) {
            LOGGER.warning("User with email: " + signupRequest.getEmail() + " already exists");
            return null;
        } else {
            //pass the item so that it added to the database
            AppUser user = new AppUser();
            user.setEmail(signupRequest.getEmail());
            user.setHashedPassword(passwordUtils.hashPassword(signupRequest.getPassword()));
            user.setFirstName(signupRequest.getFirstName());
            user.setLastName(signupRequest.getLastName());
            user.setRole("client");
            user.setCreatedAt(new Date());

            boolean userAdded = userRepository.addUser(user);
            if (userAdded) {
                return getUsersInfo(signupRequest.getEmail());
            }
            return null;
        }
    }

    public boolean userExists(RequestDto request) {
        LOGGER.info("User exists request received");
        return userRepository.userExists(request);
    }

    public boolean passwordMatches(RequestDto loginRequest) {
        return userRepository.passwordMatches(loginRequest);
    }

    public boolean updateUser(PasswordResetDto passwordResetDto) {
        return userRepository.updateUser(passwordResetDto);
    }

    public UserDto getUsersInfo(String email) {
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
