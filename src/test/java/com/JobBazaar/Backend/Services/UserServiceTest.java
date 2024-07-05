package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.RequestDto;
import com.JobBazaar.Backend.Dto.SignupRequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.Dto.UserNames;
import com.JobBazaar.Backend.Repositories.SnsRepository;
import com.JobBazaar.Backend.Repositories.UserRepository;
import com.JobBazaar.Backend.Utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final SnsRepository snsRepository = mock(SnsRepository.class);
    private final PasswordUtils passwordUtils = mock(PasswordUtils.class);
    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private RequestDto requestDto;
    private SignupRequestDto signupRequestDto;

    @BeforeEach
    void setUp() {
        requestDto = new RequestDto();
        requestDto.setEmail("test@test.com");
        requestDto.setPass("test123");

        userDto = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setHashedPassword("hashedPassword123");

        signupRequestDto = new SignupRequestDto();
        signupRequestDto.setEmail("test@test.com");
        signupRequestDto.setPassword("password123");
        signupRequestDto.setFirstName("test");
        signupRequestDto.setLastName("com");

        userService = new UserService(userRepository, snsRepository, passwordUtils);
    }

    @Test
    @DisplayName("Test creation of a user when the user already exist")
    void createUser_UserExists() {
        when(userRepository.userExists(requestDto)).thenReturn(true);

        boolean result = userService.createUser(signupRequestDto);

        assertFalse(result);
        verify(userRepository, times(1)).userExists(any(RequestDto.class));
    }

    @Test
    @DisplayName("Test creation of a user when the user doesn't exist")
    void createUser_UserDoesNotExist() {
        when(userRepository.userExists(requestDto)).thenReturn(false);
        when(passwordUtils.hashPassword(requestDto.getPassword())).thenReturn("hashedPassword123");
        when(userRepository.addUser(any(UserDto.class))).thenReturn(true);

        boolean result = userService.createUser(signupRequestDto);

        assertTrue(result);
        verify(userRepository, times(1)).userExists(any(RequestDto.class));
        verify(userRepository, times(1)).addUser(any(UserDto.class));
        verify(passwordUtils, times(1)).hashPassword(anyString());
    }

    @Test
    @DisplayName("Test to check if a user exists or not")
    void userExists() {
        when(userRepository.userExists(requestDto)).thenReturn(false);

        boolean result = userService.userExists(requestDto);

        assertFalse(result);
        verify(userRepository, times(1)).userExists(requestDto);
    }

    @Test
    @DisplayName("Test to check if a user password matches with the stored one")
    void passwordMatches() {
        when(userRepository.passwordMatches(requestDto)).thenReturn(true);

        boolean result = userService.passwordMatches(requestDto);

        assertTrue(result);
        verify(userRepository, times(1)).passwordMatches(requestDto);
    }

    @Test
    @DisplayName("Test to verify if a users password has been updated")
    void updateUser() {
        when(userRepository.updateUser(requestDto)).thenReturn(true);

        boolean result = userService.updateUser(requestDto);

        assertTrue(result);
        verify(userRepository, times(1)).updateUser(requestDto);
    }

    @Test
    void getUsersInfo() {
        UserNames names = new UserNames();
        names.setFirstName("Mohamedamin");
        names.setLastName("Mohamed");

        when(userRepository.getUsersInfo(anyString())).thenReturn(names);

        UserNames result = userService.getUsersInfo("");

        verify(userRepository, times(1)).getUsersInfo(anyString());
        assertEquals(names.getFirstName(), result.getFirstName());
        assertEquals(names.getLastName(), result.getLastName());
    }

    @Test
    void saveTopicArn() {
        doNothing().when(snsRepository).saveTopicArn(anyString(), anyString());

        userService.saveTopicArn(anyString(), anyString());

        verify(snsRepository, times(1)).saveTopicArn(anyString(), anyString());
    }

    @Test
    void subscriberAddedToTopic() throws InstantiationException, IllegalAccessException {
        when(snsRepository.addSubscriberToTopic(any(SignupRequestDto.class), anyString())).thenReturn(true);

        boolean result = userService.subscriberAddedToTopic(new SignupRequestDto(), "");

        assertTrue(result);
        verify(snsRepository).addSubscriberToTopic(any(SignupRequestDto.class), anyString());
    }


}