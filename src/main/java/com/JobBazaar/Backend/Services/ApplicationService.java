package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.ApplicationDto;
import com.JobBazaar.Backend.Repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ApplicationService {
    public final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public boolean addApplication(ApplicationDto application, Map<String, Map<String, String>> fileUploadedToS3Info) {
        return applicationRepository.addApplication(application, fileUploadedToS3Info);
    }

    public List<Map<String, String>> getJobsAppliedTo(String applicantEmail){
        return applicationRepository.getJobsAppliedTo(applicantEmail);
    }
}
