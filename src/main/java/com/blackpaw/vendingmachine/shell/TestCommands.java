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
import org.springframework.http.HttpStatus;
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

    @ShellMethod("select item")
    public String select(@ShellOption(value = "--id") long id) {
        ResponseEntity<Object> vend = vendController.select(id);
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
                    .append("please insert coins with 'vend --coins' command")
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
        ResponseEntity<Object> response = vendController.create(cashFloat);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            isReady = true;
            logger.info("Vending machine successfully created.{} ", response.getBody());
        } else {
            logger.info(response.getBody().toString());
        }
    }
}