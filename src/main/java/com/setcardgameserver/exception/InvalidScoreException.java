package com.setcardgameserver.exception;

import lombok.Getter;

@Getter
public class InvalidScoreException extends RuntimeException {
    private final String message;

    public InvalidScoreException(String message) {
        this.message = message;
    }
}
