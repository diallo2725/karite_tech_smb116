package com.melitech.karitetech.model;

public class ResponseError {
    private boolean status;
    private String message;
    private ErrorData data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ErrorData getData() {
        return data;
    }
}


