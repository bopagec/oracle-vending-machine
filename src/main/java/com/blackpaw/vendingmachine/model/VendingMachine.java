package com.blackpaw.vendingmachine.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Data
@Entity
public abstract class VendingMachine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double cashFloat;
    private String status;
    @OneToMany(mappedBy = "vendingMachine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;
    abstract String getCurrency();

    @ElementCollection
    @MapKeyColumn(name="coin_type")
    @Column(name= "count")
    private  Map<Coin, Double> denominations = new LinkedHashMap<>();
}