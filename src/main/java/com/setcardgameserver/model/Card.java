package com.setcardgameserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Card implements Serializable {
    private Color color;
    private Shape shape;
    private Quantity quantity;
}
