package com.payments.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ExceptionDTO {
    private Date date;
    private String message;

    public ExceptionDTO(String message) {
        this.date = new Date();
        this.message = message;
    }
}
