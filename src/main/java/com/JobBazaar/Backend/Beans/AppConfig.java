package com.JobBazaar.Backend.Beans;

import com.JobBazaar.Backend.Repositories.EducationRepository;
import com.JobBazaar.Backend.Repositories.SnsRepository;
import com.JobBazaar.Backend.Repositories.WorkRepository;
import com.JobBazaar.Backend.Services.EducationService;
import com.JobBazaar.Backend.Services.UserService;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import com.JobBazaar.Backend.Repositories.UserRepository;
import com.JobBazaar.Backend.Services.WorkService;
import com.JobBazaar.Backend.Utils.PasswordUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sns.SnsClient;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class AppConfig {
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder().region(Region.US_EAST_2).build();
    }

    @Bean
    public UserService userService() {
        return new UserService(userRepository(), snsRepository(), passwordutils());
    }
    @Bean
    public EducationService educationService() {
        return new EducationService(educationRepository());
    }
    @Bean
    public WorkService workService(){
        return new WorkService(workRepository());
    }
    @Bean
    public PasswordUtils passwordutils() {
        return new PasswordUtils();
    }
    @Bean
    public DynamoDbItemMapper dynamoDbItemMapper() {
        return new DynamoDbItemMapper();
    }
    @Bean
    public UserRepository userRepository() {
        return new UserRepository(dynamoDbClient(), dynamoDbItemMapper(), passwordutils(), buildSesV2Client());
    }
    @Bean
    public SnsRepository snsRepository() {
        return new SnsRepository(dynamoDbClient(), buildSnsClient());
    }
    @Bean
    public EducationRepository educationRepository() {
        return new EducationRepository(dynamoDbClient(), dynamoDbItemMapper());
    }
    @Bean
    public WorkRepository workRepository(){
        return new WorkRepository(dynamoDbClient(), dynamoDbItemMapper());
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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

    @Bean
    public SnsClient buildSnsClient() {
        return SnsClient.builder().region(Region.US_EAST_2).build();
    }

    @Bean
    public SesV2Client buildSesV2Client() {
        return SesV2Client.builder().region(Region.US_EAST_2).build();
    }
}
