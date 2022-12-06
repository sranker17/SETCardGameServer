package com.setcardgameserver.Model;

public enum Shape {
    DIAMOND("d"),
    WAVY("s"),
    OVAL("p");

    public final String label;

    Shape(String label) {
        this.label = label;
    }
}
