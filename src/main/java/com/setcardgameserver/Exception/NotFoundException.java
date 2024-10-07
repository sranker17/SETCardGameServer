package com.setcardgameserver.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends Exception {
    private final String message;

    public NotFoundException(String message) {
        this.message = message;
    }
}
