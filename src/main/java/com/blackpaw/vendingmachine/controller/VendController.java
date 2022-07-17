package com.blackpaw.vendingmachine.controller;

import com.blackpaw.vendingmachine.dto.ItemDTO;
import com.blackpaw.vendingmachine.model.*;
import com.blackpaw.vendingmachine.service.ItemService;
import com.blackpaw.vendingmachine.service.VendingMachineService;
import com.blackpaw.vendingmachine.shell.TestCommands;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.blackpaw.vendingmachine.model.Coin.*;

@RestController
@RequestMapping("/vending-machine")
public class VendController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private VendingMachineService vendingMachineService;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${vending-machine.float.min}")
    private double minCashFloat;

    @Value("${vending-machine.float.coinMax}")
    private int maxCoin;

    private static Logger logger = LoggerFactory.getLogger(VendController.class);

    @GetMapping("/create/{float}")
    public @ResponseBody ResponseEntity<Object> create(@NonNull @PathVariable("float") double cashFloat){
        vendingMachineService.reset();

        VendingMachine vendingMachine = new GBVendingMachine();

        if(cashFloat < minCashFloat){
            return new ResponseEntity<>("cash float cannot be less than " + minCashFloat, HttpStatus.BAD_REQUEST);
        }

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

        Map<Coin, Integer> gbDenominationMap = new LinkedHashMap<>() {{
            put(ONE_PENCE, null);
            put(TWO_PENCE, null);
            put(FIVE_PENCE, null);
            put(TEN_PENCE, null);
            put(TWENTY_PENCE, null);
            put(FIFTY_PENCE, null);
            put(ONE_POUND, null);
        }};

        vendingMachine.setCashFloat(cashFloat);
        vendingMachine.setItems(items);
        vendingMachine.setStatus(VendingMachine.Status.READY);
        createDenominations(vendingMachine, gbDenominationMap);
        vendingMachine.setDenominations(gbDenominationMap);

        vendingMachineService.save(vendingMachine);

        List<Item> allItems = itemService.getAll();
        logger.info("vending machine successfully created: {}", allItems);
        logger.info("vending machine is ready now: {}", vendingMachine);

        return new ResponseEntity<>(vendingMachine, HttpStatus.OK);
    }

    @GetMapping("/list")
    public @ResponseBody
    ResponseEntity<Object> listProduct() {
        List<Item> allItems = itemService.getAll();
        List<ItemDTO> itemDTOs = allItems.stream()
                .filter(item -> item.getStock() > 0)
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(itemDTOs, HttpStatus.OK);
    }

    @GetMapping("/select/{id}")
    public @ResponseBody
    ResponseEntity<Object> select(@PathVariable("id") long itemId) {
        Optional<Item> vendItem = itemService.select(itemId);
        ItemDTO itemDTO = null;

        if(vendItem.isPresent() && vendItem.get().getStock() > 0){
            itemDTO = convertToDto(vendItem.get());
        }
        return new ResponseEntity<>(itemDTO, HttpStatus.OK);

    }

    @PostMapping("/vend")
    public @ResponseBody ResponseEntity<Object> vend(@NonNull @Valid @RequestBody VendRequest vendRequest){
        Optional<Item> select = itemService.select(vendRequest.getItemId());
        DecimalFormat df = new DecimalFormat("0.00");
        if(select.isPresent()){
            double customerPaid = vendRequest.getCoins().stream().mapToDouble(value -> value.getPence()).sum() / 100;
            double itemPrice = select.get().getPrice();
            if(customerPaid < select.get().getPrice()){
                return new ResponseEntity<>(
                        "Not enough money to purchase this item.\nBalance to pay: " + df.format(itemPrice -  customerPaid) + "\nItem price: " + df.format(itemPrice)  + "\nYou paid: " + df.format(customerPaid),
                        HttpStatus.NOT_ACCEPTABLE);
            }else
                return new ResponseEntity<>("done", HttpStatus.OK);
        }
        else
            return new ResponseEntity<>("item not available", HttpStatus.NOT_FOUND);
    }

    private ItemDTO convertToDto(Item item) {
        return modelMapper.map(item, ItemDTO.class);
    }

    private void createDenominations(VendingMachine vendingMachine, Map<Coin, Integer> gbDenominationMap) {
        if (vendingMachine instanceof GBVendingMachine) {
            double total = vendingMachine.getCashFloat();

            Iterator<Map.Entry<Coin, Integer>> it = gbDenominationMap.entrySet().iterator();
            Map.Entry<Coin, Integer> previous = null;

            while (total > 0) {
                Map.Entry<Coin, Integer> next = it.hasNext() ? it.next() : previous;

                Integer pence = next.getKey().getPence();
                double val = (pence / 100.0) * maxCoin;

                if (total - val > 0) {
                    next.setValue(maxCoin);
                } else {
                    int leftOvers = Double.valueOf(total * 100).intValue() / pence;
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
