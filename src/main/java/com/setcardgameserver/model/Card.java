package com.setcardgameserver.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Card {
    private Color color;
    private Shape shape;
    private Quantity quantity;

    public Card(Color color, Shape shape, Quantity quantity) {
        this.color = color;
        this.shape = shape;
        this.quantity = quantity;
    }
}
