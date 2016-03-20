package com.candkpeters.ceol.model;

/**
 * Created by crisp on 22/01/2016.
 */
public enum DirectionType {
    Plus    (1, true),
    Minus   (1, false),
    Down    (2, false),
    Up      (2, true),
    Forward (3, true),
    Backward(3, false),
    Right   (4, true),
    Left    (4, false)
    ;

    public boolean isPositiveDirection;
    public int directionType;

    DirectionType(int directionType, boolean isPositiveDirection) {
        this.directionType = directionType;
        this.isPositiveDirection = isPositiveDirection;
    }
}
