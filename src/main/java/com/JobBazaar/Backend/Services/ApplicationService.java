package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.ApplicationDto;
import com.JobBazaar.Backend.Dto.UpdateApplicationStatusRequest;
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

    public boolean hasApplied(String applicantEmail, String jobId){
        return applicationRepository.hasApplied(applicantEmail, jobId);
    }

    public boolean deleteApplication(String applicantEmail, String jobId){
        return applicationRepository.deleteApplication(applicantEmail, jobId);
    }

    public List<Map<String, Object>> getJobsAppliedToUsers(String jobId){
        return applicationRepository.getJobsAppliedToUsers(jobId);
    }

    public boolean updateApplicationStatus(String applicantEmail, String jobId, UpdateApplicationStatusRequest statusRequest){
        return applicationRepository.updateApplicationStatus(applicantEmail, jobId, statusRequest);
    }
}
