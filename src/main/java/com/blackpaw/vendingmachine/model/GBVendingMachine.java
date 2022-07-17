package com.blackpaw.vendingmachine.model;

import lombok.Data;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.blackpaw.vendingmachine.model.Coin.*;

@Entity
@Data
public class GBVendingMachine extends VendingMachine {
    private final String currency = "GBP";
}

