package com.JobBazaar.Backend.Dto;

import com.fasterxml.jackson.databind.JsonNode;

public class ImageFetchResponse {
    private JsonNode data;
    private String message;

    public ImageFetchResponse(JsonNode data, String message) {
        this.data = data;
        this.message = message;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
