package com.JobBazaar.Backend.Repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FilesUploadRepository {
    private final Logger LOGGER = LoggerFactory.getLogger(FilesUploadRepository.class);
    private final S3Client s3Client;

    @Autowired
    public FilesUploadRepository(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public Map<String, Map<String, String>> uploadFilesTos3(Map<String, byte[]> fileContents, List<String> fileNames){
        Map<String, Map<String, String>> uploadedFilesInfo = new HashMap<>();

        int i = 0;
        for(String key : fileContents.keySet()){
            byte[] fileContent = fileContents.get(key);
            String fileName = fileNames.get(i++);
            if(fileContent.length != 0 && fileName != null && !fileName.isEmpty()){
                String keyName = "uploads/" + fileName;
                String bucket = "userfilesuploads";
                PutObjectResponse putObjectResponse = uploadFileTos3(bucket, keyName, fileContent);
                if(putObjectResponse != null){
                    Map<String, String> fileInfo = new HashMap<>();
                    fileInfo.put("S3Key", keyName);
                    fileInfo.put("Etag", putObjectResponse.eTag());
                    LOGGER.info(fileInfo.toString());
                    uploadedFilesInfo.put(key, fileInfo);
                }
                else{
                    return new HashMap<>();
                }
            }
        }
        return uploadedFilesInfo;
    }
    public PutObjectResponse uploadFileTos3(String bucket, String keyName, byte[] fileContent){
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(keyName).build();
        try{
            return s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileContent));
        }
        catch(Exception exp){
            LOGGER.error("Couldn't upload file to s3 bucket {}", exp.getMessage());
            return null;
        }
    }
}
