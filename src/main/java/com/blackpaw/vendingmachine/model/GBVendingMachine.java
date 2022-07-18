package com.blackpaw.vendingmachine.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@Entity
@Data
@EqualsAndHashCode
public class GBVendingMachine extends VendingMachine {
    private final String currency = "£";
}

