package com.blackpaw.vendingmachine.shell;


import com.blackpaw.vendingmachine.dao.ItemRepository;
import com.blackpaw.vendingmachine.dao.VendingMachineRepository;
import com.blackpaw.vendingmachine.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.*;

import static com.blackpaw.vendingmachine.model.Coin.*;

@ShellComponent
@NoArgsConstructor
public class TestCommands {
    private static final Logger logger = LoggerFactory.getLogger(TestCommands.class);
    @Value("${vending-machine.float.min}")
    private  double minCashFloat;
    @Value("${vending-machine.float.coinMax}")
    private  double maxCoin;
    private ItemRepository itemRepository;
    private VendingMachineRepository vendingMachineRepository;
    private boolean isReady = false;

    @Getter
    private final Map<Coin, Double> gbDenominationMap = new LinkedHashMap<>(){{
        put(ONE_PENCE, null);
        put(TWO_PENCE, null);
        put(FIVE_PENCE, null);
        put(TEN_PENCE, null);
        put(TWENTY_PENCE, null);
        put(FIFTY_PENCE, null);
        put(ONE_POUND, null);
    }};

    @Autowired
    public TestCommands(ItemRepository itemRepository, VendingMachineRepository vendingMachineRepository) {
        this.itemRepository = itemRepository;
        this.vendingMachineRepository = vendingMachineRepository;
    }

    @ShellMethod("create vending machine with custom float")
    public void create(@ShellOption(value = "--float") Integer cashFloat) {
        if(cashFloat < minCashFloat){
            logger.info("cash float cannot be less than {}", minCashFloat);
            return;
        }
        if(isReady){
            logger.info("vending machine is ready now.");
            return;
        }

        logger.info("loading data to the vending machine...");
        VendingMachine vendingMachine = new GBVendingMachine();

        List<Item> items = Arrays.asList(
                new Item("Water", 1.75, 20, vendingMachine),
                new Item("Energy Water", 2.50, 20, vendingMachine),
                new Item("Red bull Drink", 2.70, 20, vendingMachine),
                new Item("Coke - Original", 1.25, 20, vendingMachine),
                new Item("Pepsi - Lite", 1.25, 20, vendingMachine),
                new Item("Tuna Sandwich", 3.75, 20, vendingMachine),
                new Item("Egg Sandwich", 3.50, 20, vendingMachine),
                new Item("Beacon & Mayo Sandwich", 4.50, 20, vendingMachine),
                new Item("Snickers", 1.00, 20, vendingMachine),
                new Item("Mars", 1.50, 20, vendingMachine),
                new Item("Car Phone Charger", 5.50, 20, vendingMachine),
                new Item("Extra White Chewing Gum", 0.75, 20, vendingMachine)
        );

        vendingMachine.setCashFloat(cashFloat);
        vendingMachine.setItems(items);
        vendingMachine.setStatus(Status.READY.name());
        createDenominations(vendingMachine);
        vendingMachine.setDenominations(gbDenominationMap);
        vendingMachineRepository.save(vendingMachine);

        isReady = true;
        List<Item> allItems = itemRepository.findAll();
        logger.info("vending machine successfully created: {}", allItems);
        logger.info("vending machine is ready now: {}", vendingMachine);
    }

    public void createDenominations(VendingMachine vendingMachine){
        if(vendingMachine instanceof GBVendingMachine){
            double total = vendingMachine.getCashFloat();

            Iterator<Map.Entry<Coin, Double>> it = gbDenominationMap.entrySet().iterator();
            Map.Entry<Coin, Double> previous = null;

            while (total > 0){
                Map.Entry<Coin, Double> next = it.hasNext() ? it.next() : previous;

                Integer pence = next.getKey().getPence();
                Double val = (pence / 100.0) * maxCoin;

                if(total - val > 0){
                    next.setValue(maxCoin);
                }
                else{
                    if(next.getValue() != null){
                        next.setValue( total + next.getValue());
                    }else{
                        next.setValue(total);
                    }

                }
                total -= val;
                previous = next;
            }
        }
    }
}
