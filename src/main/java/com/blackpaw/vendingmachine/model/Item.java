package com.blackpaw.vendingmachine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private double price;
    private int stock;

    public Item(String name, double price, int stock){
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}
