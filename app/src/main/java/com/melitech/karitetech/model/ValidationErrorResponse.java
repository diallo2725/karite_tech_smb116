package com.melitech.karitetech.model;

import java.util.List;
import java.util.Map;

public class ValidationErrorResponse {
    private boolean status;
    private String message;
    private Map<String, List<String>> errors;

    public ValidationErrorResponse() {
    }

    // Getter pour le champ status
    public boolean isStatus() {
        return status;
    }

    // Setter pour le champ status
    public void setStatus(boolean status) {
        this.status = status;
    }

    // Getter pour le champ message
    public String getMessage() {
        return message;
    }

    // Setter pour le champ message
    public void setMessage(String message) {
        this.message = message;
    }

    // Getter pour le champ errors
    public Map<String, List<String>> getErrors() {
        return errors;
    }

    // Setter pour le champ errors
    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }
}

