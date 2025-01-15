package com.example.fastProductApi.exception;

public class CustomException extends Exception {
    private Throwable cause;

    public CustomException(Exception ex){
        super(ex.getMessage());
        this.cause = ex.getCause();
    }


    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        super.setStackTrace(stackTrace);
    }

    @Override
    public synchronized Throwable getCause() {
        return cause;
    }
}
