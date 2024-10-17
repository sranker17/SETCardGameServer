package com.setcardgameserver.exception;

import lombok.Getter;

@Getter
public class GameNotFoundException extends Exception {
    private final String message;

    public GameNotFoundException(String message) {
        this.message = message;
    }
}
