package com.setcardgameserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    private Color color;
    private Shape shape;
    private Quantity quantity;
}
