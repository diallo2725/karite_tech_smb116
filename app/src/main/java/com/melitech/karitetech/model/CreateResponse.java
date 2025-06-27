package com.melitech.karitetech.model;

import java.util.List;

public class CreateResponse {
    private int success_count;
    private int error_count;
    private List<SyncError> errors;

    // Getters et Setters
    public int getSuccess_count() {
        return success_count;
    }

    public void setSuccess_count(int success_count) {
        this.success_count = success_count;
    }

    public int getError_count() {
        return error_count;
    }

    public void setError_count(int error_count) {
        this.error_count = error_count;
    }

    public List<SyncError> getErrors() {
        return errors;
    }

    public void setErrors(List<SyncError> errors) {
        this.errors = errors;
    }

    // Classe interne pour représenter chaque erreur
    public static class SyncError {
        private String phone;
        private String error;

        // Getters et Setters
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}

