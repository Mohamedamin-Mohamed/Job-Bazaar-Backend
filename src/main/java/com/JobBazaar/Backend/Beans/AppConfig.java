package com.JobBazaar.Backend.Beans;

import com.JobBazaar.Backend.JwtToken.JwtAuthenticationFilter;
import com.JobBazaar.Backend.JwtToken.JwtTokenService;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import com.JobBazaar.Backend.Repositories.EducationRepository;
import com.JobBazaar.Backend.Repositories.SnsRepository;
import com.JobBazaar.Backend.Repositories.UserRepository;
import com.JobBazaar.Backend.Repositories.WorkRepository;
import com.JobBazaar.Backend.Services.EducationService;
import com.JobBazaar.Backend.Services.ImageSearchService;
import com.JobBazaar.Backend.Services.UserService;
import com.JobBazaar.Backend.Services.WorkService;
import com.JobBazaar.Backend.Utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sns.SnsClient;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class AppConfig {
    @Value("${security.jwt.secret.key}")
    private String jwtSecretKey;

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
    public WorkService workService() {
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
    public WorkRepository workRepository() {
        return new WorkRepository(dynamoDbClient(), dynamoDbItemMapper());
    }

    @Bean
    public SnsClient buildSnsClient() {
        return SnsClient.builder().region(Region.US_EAST_2).build();
    }

    @Bean
    public SesV2Client buildSesV2Client() {
        return SesV2Client.builder().region(Region.US_EAST_2).build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ImageSearchService imageSearchService() {
        return new ImageSearchService(restTemplate());
    }

    @Bean
    public JwtTokenService jwtToken() {
        return new JwtTokenService();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        var secretKey = new SecretKeySpec(jwtSecretKey.getBytes(), "");
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserService userService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());

        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/").permitAll()
                            .requestMatchers("/accounts/**").permitAll()
                            .requestMatchers("/api/**").permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .addFilterBefore(new JwtAuthenticationFilter(jwtToken()), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
