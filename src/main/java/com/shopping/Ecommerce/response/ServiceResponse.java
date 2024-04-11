package com.shopping.Ecommerce.response;

import org.springframework.http.HttpStatus;

public class ServiceResponse<T> {
    private T data;
    private String message;
    private HttpStatus status;

    public ServiceResponse(T data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}