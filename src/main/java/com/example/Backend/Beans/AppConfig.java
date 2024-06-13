package com.example.Backend.Beans;

import com.example.Backend.Mappers.DynamoDbItemMapper;
import com.example.Backend.Repositories.UserRepository;
import com.example.Backend.Services.UserService;
import com.example.Backend.Utils.PasswordUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration

public class AppConfig {
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder().region(Region.US_EAST_2).build();
    }
    @Bean
    public UserService userService (){
        return new UserService(userRepository(), passwordutils());
    }
    @Bean
    public PasswordUtils passwordutils(){
        return new PasswordUtils();
    }
    @Bean
    public DynamoDbItemMapper dynamoDbItemMapper(){
        return new DynamoDbItemMapper();
    }
    @Bean
    UserRepository userRepository(){
        return new UserRepository(dynamoDbClient(), dynamoDbItemMapper(), passwordutils());
    }
}
