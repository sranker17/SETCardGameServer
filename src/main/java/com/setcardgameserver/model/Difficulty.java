package com.setcardgameserver.model;

public enum Difficulty {
    EASY("Easy"),
    NORMAL("Normal");

    public final String label;

    Difficulty(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}