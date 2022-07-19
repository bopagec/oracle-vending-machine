package com.blackpaw.vendingmachine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Coin{
    TWO_POUND(200, 0),
    ONE_POUND(100, 0),
    FIFTY_PENCE(50, 0),
    TWENTY_PENCE(20, 0),
    TEN_PENCE(10, 0),
    FIVE_PENCE(5, 0),
    TWO_PENCE(2, 0),
    ONE_PENCE(1, 0);
    @Getter
    private int pence;
    @Getter
    private Integer count;
}
