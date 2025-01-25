package com.maxiflexy.tickethelpdeskapp.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomError<T> {

    private String responseCode;
    private String responseMessage;
    private Boolean status;
    private T data;



    public CustomError(String responseCode, String responseMessage, Boolean status) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.status = status;
    }

    public CustomError(String responseCode, String responseMessage, Boolean status, T data) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.status = status;
        this.data = data;
    }


}
