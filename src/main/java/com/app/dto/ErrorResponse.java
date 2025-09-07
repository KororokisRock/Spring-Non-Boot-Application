package com.app.dto;

public class ErrorResponse {
    private String message;
    private String className;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClassname() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ErrorResponse(Exception e) {
        this.message = e.getMessage();
        this.className = e.getClass().toString();
    }
    
}
