package com.blackpaw.vendingmachine.shell;


import com.blackpaw.vendingmachine.controller.VendController;
import com.blackpaw.vendingmachine.dao.ItemRepository;
import com.blackpaw.vendingmachine.dao.VendingMachineRepository;
import com.blackpaw.vendingmachine.dto.ItemDTO;
import com.blackpaw.vendingmachine.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
    private double minCashFloat;
    @Value("${vending-machine.float.coinMax}")
    private double maxCoin;
    private ItemRepository itemRepository;
    private VendingMachineRepository vendingMachineRepository;
    private boolean isReady = false;

    @Autowired
    private VendController vendController;

    private Item vendingItem;
    private VendingMachine vendingMachine;

    @Getter
    private final Map<Coin, Double> gbDenominationMap = new LinkedHashMap<>() {{
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

    @ShellMethod("list items to purchase")
    public String list() {
        ResponseEntity<Object> response = vendController.listProduct();
        List<ItemDTO> items = (List<ItemDTO>) response.getBody();
        StringBuilder sb = new StringBuilder();
        sb.append("ID\t\tNAME \t\t\t\tPrice (£)").append(System.getProperty("line.separator"));
        sb.append("---------------------------------------------").append(System.getProperty("line.separator"));
        items.stream().forEach(itemDTO -> {
            sb.append(itemDTO.getId()).append("\t\t").append(itemDTO.getName()).append("\t\t").append(itemDTO.getPrice()).append(System.getProperty("line.separator"));
        });

        return sb.toString();
    }

    @ShellMethod("vend item")
    public String vend(@ShellOption(value = "--id") long id) {
        ResponseEntity<Object> vend = vendController.vend(id);
        Object body = vend.getBody();

        if (body != null) {
            vendingItem = (Item) body;
        }
        if (vendingItem != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Selected item: ")
                    .append(vendingItem.getName())
                    .append(" : ").append(vendingMachine.getCurrency())
                    .append(vendingItem.getPrice())
                    .append(System.getProperty("line.separator"))
                    .append("please insert coins with 'insert --coins' command")
                    .append(System.getProperty("line.separator"))
                    .append(System.getProperty("line.separator"));

            vendingMachine.getDenominations().keySet().stream()
                    .sorted((o1, o2) -> Integer.valueOf(o1.ordinal()).compareTo(Integer.valueOf(o2.ordinal())))
                    .forEach(coin -> {
                        sb.append(coin.ordinal()).append("\t\t")
                                .append(coin.name()).append(System.getProperty("line.separator"));
                    });

            return sb.toString();
        }
        return "Item not available";
    }

    @ShellMethod("create vending machine with custom float")
    public void create(@ShellOption(value = "--float") double cashFloat) {
        if (cashFloat < minCashFloat) {
            logger.info("cash float cannot be less than {}", minCashFloat);
            return;
        }
        if (isReady) {
            logger.info("vending machine is ready now.");
            return;
        }

        logger.info("loading data to the vending machine...");
        vendingMachine = new GBVendingMachine();

        List<Item> items = Arrays.asList(
                new Item("Sparkling Water", 1.75, 20, vendingMachine),
                new Item("Energy Water", 2.50, 20, vendingMachine),
                new Item("Red bull Drink", 2.70, 20, vendingMachine),
                new Item("Coke - Original", 1.25, 20, vendingMachine),
                new Item("Pepsi - Lite", 1.25, 20, vendingMachine),
                new Item("Tuna Sandwich", 3.75, 20, vendingMachine),
                new Item("Egg Sandwich", 3.50, 20, vendingMachine),
                new Item("Mayo Sandwich", 4.50, 20, vendingMachine),
                new Item("Snickers Bar", 1.00, 20, vendingMachine),
                new Item("Mars Bar    ", 1.50, 20, vendingMachine),
                new Item("Phone Charger", 5.50, 20, vendingMachine),
                new Item("Chewing Gum   ", 0.75, 20, vendingMachine)
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

    public void createDenominations(VendingMachine vendingMachine) {
        if (vendingMachine instanceof GBVendingMachine) {
            double total = vendingMachine.getCashFloat();

            Iterator<Map.Entry<Coin, Double>> it = gbDenominationMap.entrySet().iterator();
            Map.Entry<Coin, Double> previous = null;

            while (total > 0) {
                Map.Entry<Coin, Double> next = it.hasNext() ? it.next() : previous;

                Integer pence = next.getKey().getPence();
                Double val = (pence / 100.0) * maxCoin;

                if (total - val > 0) {
                    next.setValue(maxCoin);
                } else {
                    double leftOvers = (total * 100) / pence;
                    if (next.getValue() != null) {
                        next.setValue(leftOvers + next.getValue());
                    } else {
                        next.setValue(leftOvers);
                    }

                }
                total -= val;
                previous = next;
            }
        }
    }
}
