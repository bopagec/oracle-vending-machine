package com.blackpaw.vendingmachine.fixtures;

import com.blackpaw.vendingmachine.model.Coin;
import com.blackpaw.vendingmachine.model.GBVendingMachine;
import com.blackpaw.vendingmachine.model.Item;
import com.blackpaw.vendingmachine.model.VendingMachine;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.blackpaw.vendingmachine.model.Coin.*;

@NoArgsConstructor
public class VendMachineFixture {
    public static GBVendingMachine create_GBVendingMachine_50_Float(VendingMachine.Status status) {
        Map<Coin, Integer> gbDenominationMap = new LinkedHashMap<>() {{
            put(ONE_PENCE, 50);
            put(TWO_PENCE, 50);
            put(FIVE_PENCE, 50);
            put(TEN_PENCE, 50);
            put(TWENTY_PENCE, 50);
            put(FIFTY_PENCE, 50);
            put(ONE_POUND, 6);
            put(TWO_POUND, 0);
        }};
        GBVendingMachine vendingMachine = new GBVendingMachine();

        List<Item> items = Arrays.asList(
                new Item(1, "Sparkling Water", 1.75, 20, vendingMachine),
                new Item(2,"Energy Water", 2.50, 20, vendingMachine),
                new Item(3,"Red bull Drink", 2.70, 20, vendingMachine),
                new Item(4,"Coke - Original", 1.25, 20, vendingMachine),
                new Item(5,"Pepsi - Lite", 1.25, 20, vendingMachine),
                new Item(6,"Tuna Sandwich", 3.75, 20, vendingMachine),
                new Item(7,"Egg Sandwich", 3.50, 20, vendingMachine),
                new Item(8,"Mayo Sandwich", 4.50, 20, vendingMachine),
                new Item(9,"Snickers Bar", 1.00, 20, vendingMachine),
                new Item(10,"Mars Bar    ", 1.50, 20, vendingMachine),
                new Item(11,"Phone Charger", 5.50, 20, vendingMachine),
                new Item(12,"Chewing Gum   ", 0.75, 20, vendingMachine)
        );

        vendingMachine.setId(1);
        vendingMachine.setStatus(status);
        vendingMachine.setDenominations(gbDenominationMap);
        vendingMachine.setCashFloat(50.0);
        vendingMachine.setItems(items);

        return vendingMachine;
    }
}
