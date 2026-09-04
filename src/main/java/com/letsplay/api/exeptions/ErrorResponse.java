package com.letsplay.api.exeptions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * ErrorResponse
 */
@Data
public class ErrorResponse {

    private int status;
    private String message;
    private Map<String, String> validationErrors; // For validation failures

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message; 
    }

    public ErrorResponse(int status, String message, Map<String, String> validationErrors) {
        this(status, message);
        this.validationErrors = validationErrors;
    }
}

