package com.blackpaw.vendingmachine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
public enum Coin {
    ONE_POUND(100),
    FIFTY_PENCE(50),
    TWENTY_PENCE(20),
    TEN_PENCE(10),
    FIVE_PENCE(5),
    TWO_PENCE(2),
    ONE_PENCE(1);

    private int pence;
     Coin(int pence){
        this.pence = pence;
    }

    public int getPence(){
         return this.pence;
    }

}