package com.setcardgameserver.model;

import lombok.Data;

@Data
public class Card {
    private Color color;
    private Shape shape;
    private Quantity quantity;

    public Card(Color color, Shape shape, Quantity quantity) {
        this.color = color;
        this.shape = shape;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return color.label + shape.label + quantity.label;
    }
}
