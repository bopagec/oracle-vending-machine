package com.blackpaw.vendingmachine.model;

import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class GBVendingMachine extends VendingMachine {
    private final String currency = "£";

}

