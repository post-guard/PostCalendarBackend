package top.rrricardo.postcalendarbackend.dtos;

import java.io.Serializable;

public class ResponseDTO<T> implements Serializable {
    private String message;

    private T data;

    public ResponseDTO(String message) {
        this.message = message;
    }

    public ResponseDTO(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{\"message\": \"" + message + "\"," + "\"data\": \"" + data +  "\"}";
    }
}
