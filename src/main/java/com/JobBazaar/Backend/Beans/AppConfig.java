package com.JobBazaar.Backend.Beans;

import com.JobBazaar.Backend.JwtToken.JwtAuthenticationFilter;
import com.JobBazaar.Backend.JwtToken.JwtTokenService;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import com.JobBazaar.Backend.Repositories.*;
import com.JobBazaar.Backend.Services.*;
import com.JobBazaar.Backend.Utils.PasswordUtils;
import com.JobBazaar.Backend.Utils.ShortUUIDGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sns.SnsClient;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class AppConfig {
    @Value("${security.jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${aws.accessKeyId}")
    private String accessKey;

    @Value("${aws.secretAccessKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return () -> AwsBasicCredentials.create(accessKey, secretKey);
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder().region(Region.of(region)).credentialsProvider(awsCredentialsProvider()).build();
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
    public JobService jobService() {
        return new JobService(jobRepository(), shortUUIDGenerator());
    }

    @Bean
    public JobRepository jobRepository() {
        return new JobRepository(dynamoDbClient(), dynamoDbItemMapper());
    }

    @Bean
    public ApplicationService applicationService() {
        return new ApplicationService(applicationRepository());
    }

    @Bean
    public ApplicationRepository applicationRepository() {
        return new ApplicationRepository(dynamoDbClient(), dynamoDbItemMapper(), s3Client());
    }

    @Bean
    public ShortUUIDGenerator shortUUIDGenerator() {
        return new ShortUUIDGenerator();
    }

    @Bean
    public FilesUploadService filesUploadService() {
        return new FilesUploadService(filesUploadRepository());
    }

    @Bean
    public FilesUploadRepository filesUploadRepository() {
        return new FilesUploadRepository(s3Client());
    }

    @Bean
    public SnsClient buildSnsClient() {
        return SnsClient.builder().region(Region.of(region)).credentialsProvider(awsCredentialsProvider()).build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder().region(Region.of(region)).credentialsProvider(awsCredentialsProvider()).build();
    }

    @Bean
    public SesV2Client buildSesV2Client() {
        return SesV2Client.builder().region(Region.of(region)).credentialsProvider(awsCredentialsProvider()).build();
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

    @Bean
    public EmailService emailService() {
        return new EmailService();
    }

    @Bean
    public FeedbackService feedbackService() {
        return new FeedbackService(feedbackRepository());
    }

    @Bean
    FeedbackRepository feedbackRepository() {
        return new FeedbackRepository(dynamoDbClient(), dynamoDbItemMapper());
    }

    @Bean
    ReferralsService referralsService() {
        return new ReferralsService(referralsRepository());
    }

    @Bean
    ReferralsRepository referralsRepository() {
        return new ReferralsRepository(dynamoDbClient(), dynamoDbItemMapper(), s3Client());
    }

    @Bean
    public SnsService snsService() {
        return new SnsService(snsRepository());
    }
}
