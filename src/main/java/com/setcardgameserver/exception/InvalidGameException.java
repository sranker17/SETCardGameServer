package com.setcardgameserver.exception;

import lombok.Getter;

@Getter
public class InvalidGameException extends Exception {
    private final String message;

    public InvalidGameException(String message) {
        this.message = message;
    }
}
