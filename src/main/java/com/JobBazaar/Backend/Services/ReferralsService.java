package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.FeedbackDto;
import com.JobBazaar.Backend.Dto.ReferralDto;
import com.JobBazaar.Backend.Repositories.ReferralsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ReferralsService {
    private final ReferralsRepository referralsRepository;

    @Autowired
    public ReferralsService(ReferralsRepository referralsRepository) {
        this.referralsRepository = referralsRepository;
    }

    public boolean addReferral(ReferralDto referralDto, Map<String, Map<String, String>> fileUploadedToS3Info) throws IOException {
        return referralsRepository.addReferral(referralDto, fileUploadedToS3Info);
    }

    public List<Map<String, Object>> retrieveReferrals(String referrerEmail){
        return referralsRepository.getReferrals(referrerEmail);
    }

    public List<Map<String, Object>> getAllReferrals(){
        return referralsRepository.getAllReferrals();
    }
}
