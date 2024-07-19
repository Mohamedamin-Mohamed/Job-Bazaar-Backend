package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.EducationDto;
import com.JobBazaar.Backend.Repositories.EducationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

public class EducationService {
    private EducationRepository educationRepository;

    @Autowired
    public EducationService(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    public boolean saveEducation(EducationDto educationDto) throws ParseException, JsonProcessingException {
        return educationRepository.saveEducation(educationDto);
    }

    public boolean updateEducation(EducationDto educationDto) throws ParseException, JsonProcessingException {
        return educationRepository.updateEducation(educationDto);
    }

    public boolean deleteEducation(String email) {
        return educationRepository.deleteEducation(email);
    }

    public EducationDto getEducation(String email) {
        return educationRepository.getEducation(email);
    }
}
