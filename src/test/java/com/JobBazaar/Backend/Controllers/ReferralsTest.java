package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Services.FilesUploadService;
import com.JobBazaar.Backend.Services.ReferralsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReferralsTest {
    @Mock
    ReferralsService referralsService;

    @Mock
    FilesUploadService filesUploadService;

    @InjectMocks
    Referrals referrals;

    @Test
    void addReferral_Successful() {

    }
}