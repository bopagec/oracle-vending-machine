package com.blackpaw.vendingmachine.shell;


import com.blackpaw.vendingmachine.controller.VendController;
import com.blackpaw.vendingmachine.dto.ItemDTO;
import com.blackpaw.vendingmachine.model.Coin;
import com.blackpaw.vendingmachine.model.GBVendingMachine;
import com.blackpaw.vendingmachine.model.VendRequest;
import com.blackpaw.vendingmachine.model.VendingMachine;
import com.blackpaw.vendingmachine.service.VendingMachineService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.util.ArrayList;
import java.util.List;

import static com.blackpaw.vendingmachine.model.Coin.values;

@ShellComponent
@NoArgsConstructor
public class TestCommands {
    private static final Logger logger = LoggerFactory.getLogger(TestCommands.class);

    @Autowired
    private VendController vendController;
    @Autowired
    private VendingMachineService vendingMachineService;

    private ItemDTO vendingItem;
    @Getter @Setter
    private VendingMachine vendingMachine;

    @ShellMethod(value = "list items")
    public String list() {
        Availability availability = machineAvailabilityCheck();
        if(!availability.isAvailable())  return availability.getReason();

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

    @ShellMethod(value = "select item")
    public String select(@ShellOption(value = "--id") long id) {
        Availability availability = machineAvailabilityCheck();
        if(!availability.isAvailable())  return availability.getReason();

        ResponseEntity<Object> vend = vendController.select(id);
        Object body = vend.getBody();

        if (body != null) {
            vendingItem = (ItemDTO) body;
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
                    .sorted((o1, o2) -> Integer.valueOf(o1.ordinal()).compareTo(o2.ordinal()))
                    .forEach(coin -> {
                        sb.append(coin.ordinal()).append("\t\t")
                                .append(coin.name()).append(System.getProperty("line.separator"));
                    });

            return sb.toString();
        }

        return "Item not available";
    }

    @ShellMethod(value = "vend")
    public String vend(@ShellOption("--id") int itemId, @ShellOption("--coins") int... coinIds){
        Availability availability = machineAvailabilityCheck();
        if(!availability.isAvailable())  return availability.getReason();

        VendRequest vendRequest = new VendRequest();
        vendRequest.setItemId(itemId);
        List<Coin> coins = new ArrayList<>();

        for(int i=0; i < coinIds.length; i++){
            Coin coin = values()[coinIds[i]];
            if(coin != null){
                coins.add(coin);
            }
        }

        vendRequest.setCoins(coins);

        return vendController.vend(vendRequest).getBody().toString();
    }

    @ShellMethod(value = "create machine")
    public void create(@ShellOption(value = "--float") double cashFloat) {
        ResponseEntity<Object> response = vendController.create(cashFloat);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            vendingMachine = (GBVendingMachine) response.getBody();
            logger.info("Vending machine successfully created.{} ", response.getBody());
        } else {
            logger.info(response.getBody().toString());
        }
    }

    // for some reason this method will not get automatically picked up by the caller methods
    // hence, we manually call the method to check it.
    @ShellMethodAvailability({"list", "select"})
    public Availability machineAvailabilityCheck(){
        if(vendingMachine == null){
            if(vendingMachineService.getMachine().isPresent()){
                vendingMachine = vendingMachineService.getMachine().get();
            }
        }

        if(vendingMachine != null && vendingMachine.getStatus() == VendingMachine.Status.READY){
            return Availability.available();
        }

        return Availability.unavailable("Vending Machine has not been created or not ready");
    }
}