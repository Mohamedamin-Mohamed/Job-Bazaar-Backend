package com.JobBazaar.Backend.Mappers;

import com.JobBazaar.Backend.Dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.text.ParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DynamoDbItemMapperTest {
    @InjectMocks
    @Spy
    DynamoDbItemMapper dynamoDbItemMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toDynamoDbItemMapUser() {
        AppUser user = new AppUser();
        user.setFirstName("Test");
        user.setLastName("Com");
        user.setRole("Employer");
        user.setCreatedAt(new Date());

        Map<String, AttributeValue> map = dynamoDbItemMapper.toDynamoDbItemMap(user);
        assertNotNull(map);
        assertEquals(AttributeValue.builder().s("Employer").build(), map.get("role"));
        assertEquals(AttributeValue.builder().s(String.valueOf(new Date())).build(), map.get("createdAt"));
    }

    @Test
    void toDynamoDbItemEducation() throws ParseException, JsonProcessingException {
        EducationDto educationDto = new EducationDto();
        educationDto.setEmail("test@test.com");
        educationDto.setMajor("Computer Science");
        educationDto.setStartDate("01-01-2024");

        Map<String, AttributeValue> map = dynamoDbItemMapper.toDynamoDbItemMap(educationDto);

        assertNotNull(map);
        assertEquals(AttributeValue.builder().s("Computer Science").build(), map.get("major"));
    }

    @Test
    void toDynamoDbItemWorkExperience() throws ParseException, JsonProcessingException {
        WorkDto workDto = new WorkDto();
        workDto.setCompany("My Company");
        workDto.setTittle("CEO");
        workDto.setEndDate("01-01-2024");

        Map<String, AttributeValue> map = dynamoDbItemMapper.toDynamoDbItemMap(workDto);

        assertNotNull(map);
        assertEquals(AttributeValue.builder().s("CEO").build(), map.get("title"));
    }

    @Test
    void toDynamoDbItemPasswordReset() {
        PasswordResetDto passwordResetDto = new PasswordResetDto();
        passwordResetDto.setEmail("test@test.com");
        passwordResetDto.setFirstName("Test");
        passwordResetDto.setPassword("Password");

        String hashedPassword = "ddid949494jjd1sass1";
        Map<String, AttributeValue> map = dynamoDbItemMapper.toDynamoDbItemMap(passwordResetDto, hashedPassword);

        assertNotNull(map);
        assertEquals(AttributeValue.builder().s(hashedPassword).build(), map.get("hashedPassword"));
    }

    @Test
    void toDynamoDbItemJobPost() {
        JobPostRequest postRequest = new JobPostRequest();
        postRequest.setEmployerEmail("test@test.com");
        postRequest.setJobFunction("Marketing");
        postRequest.setPostedDate("01-01-2024");

        Map<String, AttributeValue> map = dynamoDbItemMapper.toDynamoDbItemMap(postRequest);

        assertNotNull(map);
        assertEquals(AttributeValue.builder().s("01-01-2024").build(), map.get("postedDate"));
    }

    @Test
    void toDynamoDbItem_Application_Document_Details_NotNull_Contains_BothKeys_Resume_And_AdditionalDoc() {
        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setCity("Minneapolis");
        applicationDto.setEmployerEmail("test@test.com");
        applicationDto.setPostalCode("1111");

        Map<String, Map<String, String>> documentDetails = new HashMap<>();
        documentDetails.computeIfAbsent("resume", k -> new HashMap<>()).put("s3KeyName", "keyResumeTest");
        documentDetails.computeIfAbsent("additionalDoc", k -> new HashMap<>()).put("s3KeyName", "keyAdditionalDocTest");

        Map<String, AttributeValue> map = dynamoDbItemMapper.toDynamoDbItemMap(applicationDto, documentDetails);

        AttributeValue resumeDetails = map.get("resumeDetails");
        Map<String, AttributeValue> resumeMap = resumeDetails.m();
        String resumeKeyName = resumeMap.get("s3KeyName").s();

        AttributeValue additionalDocDetails = map.get("additionalDocDetails");
        Map<String, AttributeValue> additionalDocMap = additionalDocDetails.m();
        String additionalDocKeyName = additionalDocMap.get("s3KeyName").s();

        assertNotNull(map);
        assertEquals("keyResumeTest", resumeKeyName);
        assertEquals("keyAdditionalDocTest", additionalDocKeyName);
        assertEquals("Minneapolis", map.get("city").s());
    }

    @Test
    void toDynamoDbItem_Application_Document_Details_Null() {
        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setCity("Minneapolis");
        applicationDto.setEmployerEmail("test@test.com");
        applicationDto.setPostalCode("1111");

        Map<String, AttributeValue> map = dynamoDbItemMapper.toDynamoDbItemMap(applicationDto, null);

        assertNotNull(map);
        assertFalse(map.containsKey("resumeDetails"));
        assertFalse(map.containsKey("additionalDocDetails"));
    }

    @Test
    void toDynamoDbItem_Application_Document_Details_NotNull_Contains_Key_Resume() {
        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setCity("Minneapolis");
        applicationDto.setEmployerEmail("test@test.com");
        applicationDto.setPostalCode("1111");

        Map<String, Map<String, String>> documentDetails = new HashMap<>();
        documentDetails.computeIfAbsent("resume", k -> new HashMap<>()).put("s3KeyName", "keyResumeTest");

        Map<String, AttributeValue> map = dynamoDbItemMapper.toDynamoDbItemMap(applicationDto, documentDetails);

        AttributeValue resumeDetails = map.get("resumeDetails");
        Map<String, AttributeValue> resumeMap = resumeDetails.m();
        String resumeKeyName = resumeMap.get("s3KeyName").s();

        assertNotNull(map);
        assertEquals("keyResumeTest", resumeKeyName);
        assertFalse(map.containsKey("additionalDocDetails"));
        assertEquals("Minneapolis", map.get("city").s());
    }

    @Test
    void toDynamoDbItem_Application_Document_Details_NotNull_Contains_Key_AdditionalDoc() {
        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setCity("Minneapolis");
        applicationDto.setEmployerEmail("test@test.com");
        applicationDto.setPostalCode("1111");

        Map<String, Map<String, String>> documentDetails = new HashMap<>();
        documentDetails.computeIfAbsent("additionalDoc", k -> new HashMap<>()).put("s3KeyName", "keyAdditionalDocTest");

        Map<String, AttributeValue> map = dynamoDbItemMapper.toDynamoDbItemMap(applicationDto, documentDetails);

        AttributeValue additionalDocDetails = map.get("additionalDocDetails");
        Map<String, AttributeValue> additionalDocMap = additionalDocDetails.m();
        String additionalDocKeyName = additionalDocMap.get("s3KeyName").s();

        assertNotNull(map);
        assertEquals("keyAdditionalDocTest", additionalDocKeyName);
        assertFalse(map.containsKey("resumeDetails"));
        assertEquals("Minneapolis", map.get("city").s());
    }

    @Test
    void convertMapToDynamoDbMap() {
        Map<String, String> map = new HashMap<>();
        map.put("resumeKeyName", "keyResumeTest");
        map.put("additionalDocKeyName", "keyAdditionalDocTest");

        AttributeValue attribValue = dynamoDbItemMapper.convertMapToDynamoDbMap(map);
        Map<String, AttributeValue> attribValueMap = attribValue.m();

        assertNotNull(attribValue);
        assertEquals(AttributeValue.builder().s("keyResumeTest").build(), attribValueMap.get("resumeKeyName"));
    }

    @Test
    void toDynamoDbItemMapJobsAppliedTo() {
        List<Map<String, AttributeValue>> mapList = new ArrayList<>();
        Map<String, AttributeValue> job1 = new HashMap<>();
        job1.put("applicantEmail", AttributeValue.builder().s("test1@test.com").build());
        job1.put("applicationDate", AttributeValue.builder().s("01-01-2024").build());

        Map<String, AttributeValue> job2 = new HashMap<>();
        job2.put("applicantEmail", AttributeValue.builder().s("test2@test.com").build());
        job2.put("applicationStatus", AttributeValue.builder().s("InReview").build());

        mapList.add(job1);
        mapList.add(job2);

        List<Map<String, String>> resultList = dynamoDbItemMapper.toDynamoDbItemMap(mapList);
        Map<String, String> stringMapIndex0 = resultList.get(0);
        Map<String, String> stringMapIndex1 = resultList.get(1);
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertEquals("01-01-2024", stringMapIndex0.get("applicationDate"));
        assertEquals("InReview", stringMapIndex1.get("applicationStatus"));
    }

    @Test
    void toDynamoDbItemMap_Convert_AttributeMap_To_StringMap() {
        Map<String, AttributeValue> attribValMap = new HashMap<>();
        attribValMap.put("applicantEmail", AttributeValue.builder().s("test@test.com").build());

        Map<String, String> stringMap = dynamoDbItemMapper.toDynamoDbItemMap(attribValMap);

        assertNotNull(stringMap);
        assertEquals("test@test.com", stringMap.get("applicantEmail"));
    }

    @Test
    void toDynamoDbItemMapFeedback() {
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setApplicantEmail("test@test.com");
        feedbackDto.setJobId("2024abide");
        feedbackDto.setFeedbackDate("01-01-2024");
        feedbackDto.setStatus("Rejected");

        Map<String, AttributeValue> attribValMap = dynamoDbItemMapper.toDynamoDbItemMap(feedbackDto);

        assertNotNull(attribValMap);
        assertEquals("Rejected", attribValMap.get("status").s());
    }

    @Test
    void toDynamoDbItemMap_Referral_Document_Details_NotNull_ContainsKey() {
        ReferralDto referralDto = new ReferralDto();
        referralDto.setReferrerEmail("test@test.com");
        referralDto.setReferrerName("Test Com");
        referralDto.setFileName("Resume Test.pdf");
        referralDto.setCreatedAt("01-01-2024");

        Map<String, Map<String, String>> documentDetails = new HashMap<>();
        documentDetails.computeIfAbsent("refereeResumeFile", k -> new HashMap<>()).put("s3KeyName", "keyResumeTest");

        Map<String, AttributeValue> map = dynamoDbItemMapper.toDynamoDbItemMap(referralDto, documentDetails);

        AttributeValue resumeDetails = map.get("resumeDetails");
        Map<String, AttributeValue> resumeMap = resumeDetails.m();
        String resumeKeyName = resumeMap.get("s3KeyName").s();

        assertNotNull(map);
        assertEquals("keyResumeTest", resumeKeyName);
        assertEquals("Resume Test.pdf", map.get("fileName").s());
    }

    @Test
    void toDynamoDbItemMap_Referral_Document_Details_Null() {
        ReferralDto referralDto = new ReferralDto();
        referralDto.setReferrerEmail("test@test.com");
        referralDto.setReferrerName("Test Com");
        referralDto.setFileName("Resume Test.pdf");
        referralDto.setCreatedAt("01-01-2024");

        Map<String, AttributeValue> map = dynamoDbItemMapper.toDynamoDbItemMap(referralDto, null);

        assertNotNull(map);
        assertFalse(map.containsKey("resumeDetails"));
    }

    @Test
    void toDynamoDbItemMap_Convert_MapList_To_FeedbackDto() {
        List<Map<String, AttributeValue>> mapList = new ArrayList<>();

        Map<String, AttributeValue> feedback1 = new HashMap<>();
        feedback1.put("applicantEmail", AttributeValue.builder().s("test1@test.com").build());
        feedback1.put("jobId", AttributeValue.builder().s("2024sass").build());
        feedback1.put("feedbackDate", AttributeValue.builder().s("01-01-2024").build());
        feedback1.put("feedback", AttributeValue.builder().s("Application is being reviewed").build());
        feedback1.put("status", AttributeValue.builder().s("InReview").build());

        Map<String, AttributeValue> feedback2 = new HashMap<>();
        feedback2.put("applicantEmail", AttributeValue.builder().s("test2@test.com").build());
        feedback2.put("jobId", AttributeValue.builder().s("2024dist").build());
        feedback2.put("feedbackDate", AttributeValue.builder().s("12-31-2024").build());
        feedback2.put("feedback", AttributeValue.builder().s("Application has been rejected").build());
        feedback2.put("status", AttributeValue.builder().s("Rejected").build());

        mapList.add(feedback1);
        mapList.add(feedback2);

        List<FeedbackDto> resultList = dynamoDbItemMapper.toDynamoDbFeedbackDto(mapList);
        FeedbackDto feedbackDto1 = resultList.get(0);
        FeedbackDto feedbackDto2 = resultList.get(1);

        assertNotNull(resultList);
        assertEquals("test1@test.com", feedbackDto1.getApplicantEmail());
        assertEquals("Rejected", feedbackDto2.getStatus());
    }
}