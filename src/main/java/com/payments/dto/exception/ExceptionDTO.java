package com.payments.dto.exception;

import java.util.Date;

public class ExceptionDTO {
    private Date date;
    private String message;

    public ExceptionDTO() {
        // Empty constructor for Spring boot
    }

    public ExceptionDTO(Date date, String message) {
        this.date = date;
        this.message = message;
    }

    public ExceptionDTO(String message) {
        this.date = new Date();
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
