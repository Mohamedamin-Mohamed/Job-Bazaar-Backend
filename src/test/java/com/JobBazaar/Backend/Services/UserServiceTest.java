package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.RequestDto;
import com.JobBazaar.Backend.Dto.SignupRequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
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
    void testCreateUser_UserExists() {
       when(userRepository.userExists(requestDto)).thenReturn(true);

       boolean result = userService.createUser(signupRequestDto);

       assertFalse(result);
       verify(userRepository, times(1)).userExists(any(RequestDto.class));
    }

    @Test
    @DisplayName("Test creation of a user when the user doesn't exist")
    void testCreateUser_UserDoesNotExist() {
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
    void testUserExists(){
        when(userRepository.userExists(requestDto)).thenReturn(false);

        boolean result = userService.userExists(requestDto);

        assertFalse(result);
        verify(userRepository, times(1)).userExists(requestDto);
    }

    @Test
    @DisplayName("Test to check if a user password matches with the stored one")
    void testPasswordMatches(){
        when(userRepository.passwordMatches(requestDto)).thenReturn(true);

        boolean result = userService.passwordMatches(requestDto);

        assertTrue(result);
        verify(userRepository, times(1)).passwordMatches(requestDto);
    }

    @Test
    @DisplayName("Test to verify if a users password has been updated")
    void testUpdateUser(){
        when(userRepository.updateUser(requestDto)).thenReturn(true);

        boolean result = userService.updateUser(requestDto);

        assertTrue(result);
        verify(userRepository, times(1)).updateUser(requestDto);
    }

}