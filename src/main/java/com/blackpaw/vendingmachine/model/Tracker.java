package com.blackpaw.vendingmachine.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Tracker {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int twoPound;
    private int onePound;
    private int fiftyPence;
    private int twentyPence;
    private int tenPence;
    private int fivePence;
    private int twoPence;
    private int onePence;

    @OneToOne(cascade = CascadeType.ALL)
    private Item item;

    public void updateCoins(Coin coin, int count){
        switch (coin){
            case TWO_POUND:
                setTwoPound(count);
                break;
            case ONE_POUND:
                setOnePound(count);
                break;
            case FIFTY_PENCE:
                setFiftyPence(count);
                break;
            case TWENTY_PENCE:
                setTwentyPence(count);
                break;
            case TEN_PENCE:
                setTenPence(count);
                break;
            case FIVE_PENCE:
                setFivePence(count);
                break;
            case TWO_PENCE:
                setTwoPence(count);
                break;
            case ONE_PENCE:
                setOnePence(count);
                break;
        }
    }
}
