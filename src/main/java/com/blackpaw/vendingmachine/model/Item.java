package com.blackpaw.vendingmachine.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private double price;
    private int stock;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vending_machine_id")
    @JsonManagedReference
    private VendingMachine vendingMachine;

    public Item(String name, double price, int stock, VendingMachine vendingMachine){
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.vendingMachine = vendingMachine;
    }
}
