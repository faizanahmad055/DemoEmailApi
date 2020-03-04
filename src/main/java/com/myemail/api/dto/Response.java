package com.myemail.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Response {
    private boolean success;
    private String errorDescription;

    @Override
    public String toString() {
        return "Response{" +
                "success='" + success + '\'' +
                ", errorDescription='" + errorDescription + '\'' +
                '}';
    }
}
