package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.WorkDto;
import com.JobBazaar.Backend.Repositories.WorkRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

public class WorkService {
    private final WorkRepository workRepository;

    @Autowired
    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    public boolean saveWorkExperience(WorkDto workDto) throws ParseException, JsonProcessingException {
        return workRepository.saveWorkExperience(workDto);
    }

    public boolean deleteWorkExperience(String email) {
        return workRepository.deleteWorkExperience(email);
    }

    public WorkDto getWorkExperience(String email) {
        return workRepository.getWorkExperience(email);
    }
}
