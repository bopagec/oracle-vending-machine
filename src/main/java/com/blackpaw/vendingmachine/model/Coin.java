package com.blackpaw.vendingmachine.model;

public enum Coin{
    TWO_POUND(200),
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
