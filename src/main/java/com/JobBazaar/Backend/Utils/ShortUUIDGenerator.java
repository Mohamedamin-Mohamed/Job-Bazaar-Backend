package com.JobBazaar.Backend.Utils;

import java.time.Year;
import java.util.UUID;

public class ShortUUIDGenerator {
    public String generateShortUUID(){
        String currentYear = String.valueOf(Year.now().getValue());
        String baseUUID = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);

        return currentYear + baseUUID;
    }
}
