package com.JobBazaar.Backend.Beans;

import com.JobBazaar.Backend.Services.UserService;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import com.JobBazaar.Backend.Repositories.UserRepository;
import com.JobBazaar.Backend.Utils.PasswordUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.springframework.security.config.Customizer.withDefaults;

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
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/**").permitAll();
                    //auth.requestMatchers("/api/client/home").permitAll();
                    auth.anyRequest().authenticated();
                })
                .csrf(csrf -> {
                    csrf.ignoringRequestMatchers("/**");
                })
                .formLogin(withDefaults())
                .build();
    }
}
