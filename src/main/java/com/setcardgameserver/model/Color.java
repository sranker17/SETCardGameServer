package com.setcardgameserver.model;

public enum Color {
    GREEN("g"),
    RED("r"),
    PURPLE("p");

    public final String label;

    Color(String label) {
        this.label = label;
    }
}
