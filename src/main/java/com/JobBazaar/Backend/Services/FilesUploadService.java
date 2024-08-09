package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Repositories.FilesUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues;

import java.util.List;
import java.util.Map;

@Service
public class FilesUploadService {
    private final FilesUploadRepository filesUploadRepository;

    @Autowired
    public FilesUploadService(FilesUploadRepository filesUploadRepository) {
        this.filesUploadRepository = filesUploadRepository;
    }

    public Map<String, Map<String, String>> uploadFile(Map<String, byte[]> map, List<String> fileNames) {
        return filesUploadRepository.uploadFilesTos3(map, fileNames);
    }
}
