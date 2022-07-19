package com.blackpaw.vendingmachine.controller;

import com.blackpaw.vendingmachine.dto.ItemDTO;
import com.blackpaw.vendingmachine.model.*;
import com.blackpaw.vendingmachine.service.ItemService;
import com.blackpaw.vendingmachine.service.TrackerService;
import com.blackpaw.vendingmachine.service.VendingMachineService;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Autowired
    private TrackerService trackerService;

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
            put(ONE_PENCE, 0);
            put(TWO_PENCE, 0);
            put(FIVE_PENCE, 0);
            put(TEN_PENCE, 0);
            put(TWENTY_PENCE, 0);
            put(FIFTY_PENCE, 0);
            put(ONE_POUND, 0);
            put(TWO_POUND, 0);
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
        Optional<Item> optSelectedItem = itemService.select(vendRequest.getItemId());
        DecimalFormat df = new DecimalFormat("0.00");
        Optional<VendingMachine> machine = vendingMachineService.getMachine();

        if(!machine.isPresent() || machine.get().getStatus() != VendingMachine.Status.READY){
            return new ResponseEntity<>("Vending Machine not available. Please try again later.", HttpStatus.OK);
        }

        if(optSelectedItem.isPresent()){
            double customerPaid = vendRequest.getCoins().stream().mapToDouble(value -> value.getPence()).sum() / 100;
            double itemPrice = optSelectedItem.get().getPrice();
            if(customerPaid < itemPrice){
                return new ResponseEntity<>(
                        "Not enough money to purchase this item.\nBalance to pay: " + df.format(itemPrice -  customerPaid) + "\nItem price: " + df.format(itemPrice)  + "\nYou paid: " + df.format(customerPaid),
                        HttpStatus.NOT_ACCEPTABLE);
            }
            else if(customerPaid == itemPrice){
                itemService.updateItem(optSelectedItem.get(), false);

                vendingMachineService.updateMachine(vendRequest, customerPaid, true);
                updateTracker(optSelectedItem.get(), Collections.emptyList());
                return new ResponseEntity<>("Item successfully vended. Thank you.", HttpStatus.OK);
            }
            else{
                List<Coin> balanceInCoins = returnCustomerBalance(vendRequest, customerPaid, optSelectedItem.get());
                updateTracker(optSelectedItem.get(), balanceInCoins);
                return new ResponseEntity<>(
                        "Item successfully vended. Thank you.\nTake the balance\n"+
                        balanceInCoins,
                        HttpStatus.OK);
            }
        }
        else
            return new ResponseEntity<>("item not available", HttpStatus.NOT_FOUND);
    }

    private void updateTracker(Item item, List<Coin> coins){
        Tracker tracker = new Tracker();
        tracker.setItem(item);

        Map<Coin, List<Coin>> allCoins = coins.stream()
                .collect(Collectors.groupingBy(coin -> coin));

        allCoins.entrySet().stream().forEach(coinListEntry -> {
            tracker.updateCoins(coinListEntry.getKey(), coinListEntry.getValue().size());
        });

        trackerService.save(tracker);

    }

    private List<Coin> returnCustomerBalance(VendRequest vendRequest, double customerPaid, Item item) {
        BigDecimal customerPaidBD = new BigDecimal(customerPaid).setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal itemPriceBD = new BigDecimal(item.getPrice()).setScale(2, RoundingMode.HALF_DOWN);

        Long balanceInPence = customerPaidBD.subtract(itemPriceBD).multiply(new BigDecimal(100)).longValue();

        List<Coin> coinsToPay = new ArrayList<>();

        // first pay in the vending machine and will reverse if not possible to pay the balance
        vendingMachineService.updateMachine(vendRequest, customerPaid, true);
        // update the item  and will reverse if not possible to pay the balance
        itemService.updateItem(item, false);

        VendingMachine vendingMachine = vendingMachineService.getMachine().get();

        Comparator<Coin> coinComparator =(o1, o2) -> Integer.valueOf(o1.ordinal()).compareTo(o2.ordinal());

        List<Coin> sortedDenominations = vendingMachine.getDenominations().keySet().stream()
                .sorted(coinComparator::compare)
                .collect(Collectors.toList());

        for(Coin coin : sortedDenominations){
            long coins = balanceInPence / coin.getPence();
            // vending machine has coins to pay
            if(vendingMachine.getDenominations().get(coin) > coins){
                // update the coins to pay map
                for(int i=0; i < coins; i++){
                    coinsToPay.add(coin);
                }
                balanceInPence = balanceInPence % coin.getPence();

                if(balanceInPence == 0)
                    break;
            }
        }

        if(balanceInPence == 0){
            VendRequest balanceVendRequest = new VendRequest();
            balanceVendRequest.setItemId(item.getId());
            balanceVendRequest.setCoins(coinsToPay);

            vendingMachineService.updateMachine(balanceVendRequest, customerPaid - item.getPrice(), false );
            return coinsToPay;
        }
        else{
            // rollback process, cannot vendor the item
            itemService.updateItem(item, true);
            vendingMachineService.updateMachine(vendRequest, customerPaid, false);
        }
        return null;
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
