package com.setcardgameserver.exception;

import lombok.Getter;

@Getter
public class InvalidUsernameException extends RuntimeException {
    private final String message;

    public InvalidUsernameException(String message) {
        this.message = message;
    }
}
