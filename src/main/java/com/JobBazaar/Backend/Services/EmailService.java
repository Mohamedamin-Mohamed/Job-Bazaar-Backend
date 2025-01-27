package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.MailjetDto;
import com.JobBazaar.Backend.config.MailjetConfig;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

@Service
public class EmailService {
    private final MailjetConfig mailjetConfig;

    @Value("${mailjet.senderEmail}")
    String senderEmail;

    @Value("${mailjet.senderName}")
    String senderName;

    public Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    public EmailService(MailjetConfig mailjetConfig) {
        this.mailjetConfig = mailjetConfig;
    }

    public void sendWelcomeOrResetEmail(String recipientEmail, String recipientName, String type) throws IOException {
        LOGGER.info("Sending welcome or reset email for recipient : {}", recipientName);

        boolean typeIsWelcome = checkType(type);
        // read the file content
        String template = typeIsWelcome ? getEmailTemplate("welcome_email.txt") : getEmailTemplate("forgot_password.txt");

        // extract the subject (first line) and remove it from the body
        String[] templateLines = template.split("\\r?\\n");
        String subject = templateLines[0].replace("Subject: ", "");

        // combine the rest of the template after removing the subject
        String bodyContent = String.join("\n", Arrays.copyOfRange(templateLines, 1, templateLines.length));

        String target = typeIsWelcome ? "Dear {}" : "Hello {}";
        String replacement = (typeIsWelcome ? "Dear " : "Hello ") + recipientName;

        // replace the placeholder with recipientName while keeping the format intact
        String updatedContent = bodyContent.replace(target, replacement);
        MailjetDto mailjetDto = new MailjetDto();
        mailjetDto.setRecipientEmail(recipientEmail);
        mailjetDto.setRecipientName(recipientName);
        mailjetDto.setSubject(subject);
        mailjetDto.setBodyContent(updatedContent);
        setMailjetConfig(mailjetDto);
    }

    public void sendJobRelatedEmail(String recipientEmail, String recipientName, String role, boolean isNewJob, String position) throws IOException {
        LOGGER.info("Sending job related email for recipient : {}", recipientName);

        boolean roleIsEmployer = checkRole(role);

        // select the appropriate template file
        String templateFile = roleIsEmployer ? (isNewJob ? "job_uploaded.txt" : "job_edit.txt") : "job_applied.txt";
        String template = getEmailTemplate(templateFile);

        // extract subject and body content
        String[] templateLines = template.split("\\r?\\n");
        String subject = templateLines[0].replace("Subject: ", "");
        String bodyContent = String.join("\n", Arrays.copyOfRange(templateLines, 1, templateLines.length));

        // replace placeholders in the subject and body
        subject = subject.replace("{position}", position);
        bodyContent = bodyContent.replace("{position}", position)
                .replace("Dear {}", "Dear " + recipientName).replace("for {}", String.format("for %s", position));

        MailjetDto mailjetDto = new MailjetDto();
        mailjetDto.setRecipientEmail(recipientEmail);
        mailjetDto.setRecipientName(recipientName);
        mailjetDto.setSubject(subject);
        mailjetDto.setBodyContent(bodyContent);
        setMailjetConfig(mailjetDto);
    }

    public void sendAccountDeletionEmail(String recipientEmail, String recipientName) throws IOException {
        LOGGER.info("Sending account deletion notification for recipient : {}", recipientName);

        // read the file content
        String template = getEmailTemplate("account_deletion.txt");

        // extract the subject (first line) and remove it from the body
        String[] templateLines = template.split("\\r?\\n");
        String subject = templateLines[0].replace("Subject: ", "");

        // combine the rest of the template after removing the subject
        String bodyContent = String.join("\n", Arrays.copyOfRange(templateLines, 1, templateLines.length));

        String target = "Dear {}";
        String replacement = "Dear " + recipientName;

        // replace the placeholder with recipientName while keeping the format intact
        String updatedContent = bodyContent.replace(target, replacement);
        MailjetDto mailjetDto = new MailjetDto();
        mailjetDto.setRecipientEmail(recipientEmail);
        mailjetDto.setRecipientName(recipientName);
        mailjetDto.setSubject(subject);
        mailjetDto.setBodyContent(updatedContent);
        setMailjetConfig(mailjetDto);
    }

    public void setMailjetConfig(MailjetDto mailjetDto) {
        try {
            // convert to HTML with proper formatting
            String htmlContent = mailjetDto.getBodyContent().replace("\n", "<br>");

            // send the email using Mailjet API
            MailjetClient client = mailjetConfig.connect();
            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", senderEmail)
                                            .put("Name", senderName))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", mailjetDto.getRecipientEmail())
                                                    .put("Name", mailjetDto.getRecipientName())))
                                    .put(Emailv31.Message.SUBJECT, mailjetDto.getSubject())
                                    .put(Emailv31.Message.TEXTPART, mailjetDto.getBodyContent())
                                    .put(Emailv31.Message.HTMLPART, htmlContent)));

            MailjetResponse response = client.post(request);

            if (response.getStatus() == 200) {
                LOGGER.info("Email sent successfully: {}", response.getData());
            } else {
                LOGGER.error("Error sending email: {}", response.getData());
            }
        } catch (Exception e) {
            LOGGER.error("Error sending email: {}", e.getMessage());
            throw new RuntimeException("Error sending email", e);
        }
    }

    public String getEmailTemplate(String fileName) throws IOException {
        // access the template file from the resources directory
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(String.format("Email/%s", fileName));

        if (inputStream == null) {
            throw new IllegalArgumentException("Template file not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder templateBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                templateBuilder.append(line).append("\n");
            }

            return templateBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error reading email template", e);
        }
    }

    public boolean checkType(String type) {
        return type.equals("welcome_email");
    }

    public boolean checkRole(String role) {
        return role.equalsIgnoreCase("Employer");
    }

}
