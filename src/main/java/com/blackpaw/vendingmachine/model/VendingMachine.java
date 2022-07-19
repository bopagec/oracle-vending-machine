package com.blackpaw.vendingmachine.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import org.hibernate.annotations.Cascade;

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
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(mappedBy = "vendingMachine", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Item> items;
    public abstract String getCurrency();

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="coin_type")
    @Column(name= "count")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private  Map<Coin, Integer> denominations = new LinkedHashMap<>();

    public enum Status{
        READY, NOT_READY, SERVICE
    }
}