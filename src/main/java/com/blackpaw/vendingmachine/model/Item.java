package com.blackpaw.vendingmachine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private double price;
    private int stock;

    @ManyToOne
    @JoinColumn(name = "vending_machine_id")
    private VendingMachine vendingMachine;

    public Item(String name, double price, int stock, VendingMachine vendingMachine){
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.vendingMachine = vendingMachine;
    }
}
