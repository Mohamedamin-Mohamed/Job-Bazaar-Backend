# Job-Bazaar-Backend
This repository contains the backend code for the Job Bazaar application. It is built using Spring Boot and Java, with the application hosted on AWS. The backend handles job postings, applications, referrals, and user management while integrating 
with various AWS services such as DynamoDB, S3, and SNS.

## Technologies Used

Java  
Spring Boot: For creating a RESTful API and handling the application’s backend logic.  
AWS DynamoDB: Used as the primary database for storing job-related data.  
AWS S3: For storing user resumes and additional documents securely.  
AWS SNS: Simple Notification Service (SNS) for subscribing users to a topic for updates when they sign up.  
AWS SDK: To integrate with AWS services such as S3, SNS, and DynamoDB.  

## Architecture
This project follows the Controller-Service-Repository design pattern for separating concerns:

Controller Layer: Handles HTTP requests, validates input, and returns responses.  
Service Layer: Contains the business logic for the application, interacting with the repository layer.  
Repository Layer: Directly interacts with the DynamoDB database for CRUD operations.  

## AWS Services
1. ## Amazon DynamoDB:
   I use DynamoDB for storing all job-related and user-related data. I manage the following tables:

    a. Applications: Stores data for job applications.  
    b. Education: Stores user education background.  
    c. Feedback: Stores feedback for job applications.  
    d. Jobs: Stores information on job postings.  
    e. Referrals: Stores referrals made by users.  
    f. Store_Topic_Arn: Stores the SNS Topic ARNs for each user to send notifications.  
    g. Users: Stores user information.  
    h. Work: Stores user work experience.  
2. ## Amazon S3:
    User resumes and additional documents (such as referrals) are uploaded to and stored in an S3 bucket.  
    The backend handles the process of uploading and retrieving files from S3.

3. ## Amazon SNS:
   SNS is used to subscribe users to a topic when they first sign up.  
   Notifications are sent to users through SNS for job updates and application statuses.
   
## Security and Credentials
Sensitive information such as API keys, secretKey, accessKeyId, and secretAccessKey required for AWS services (S3, SNS, and DynamoDB) are securely stored in the application.yml file and are not included in the public repository for security reasons.  
Make sure to set up your own application.yml with the required credentials before running the application.

aws:
   accessKeyId: YOUR_ACCESS_KEY_ID  
   secretAccessKey: YOUR_SECRET_ACCESS_KEY  
   region: YOUR_AWS_REGION  
   s3:  
      bucket-name: YOUR_S3_BUCKET_NAME  
   sns:  
      topic-arn: YOUR_SNS_TOPIC_ARN  

## Getting Started
### Prerequisites
Java 11+  
Maven  
AWS Account with access to DynamoDB, S3, and SNS

### Installation
1. Clone the repository:
   git clone [https://github.com/your-username/Job-Bazaar-Backend.git]  
   cd Job-Bazaar-Backend  
2. Configure AWS Credentials: Set your AWS credentials for S3, SNS, and DynamoDB access.
3. Run the application: mvn spring-boot:run
4. Access the API: The API will be available at [http://localhost:8080]

## Contributing
Contributions are welcome! Please follow the typical Git workflow:

1. Fork the repository.
2. Create a new feature branch.
3. Commit and push your changes.
4. Submit a pull request for review.

This README gives an overview of the Job-Bazaar-Backend project, including the architecture, technologies, database structure, AWS services, and security considerations for handling credentials.

