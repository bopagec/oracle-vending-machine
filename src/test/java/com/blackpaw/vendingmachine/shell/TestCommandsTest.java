package com.blackpaw.vendingmachine.shell;

import com.blackpaw.vendingmachine.controller.VendController;
import com.blackpaw.vendingmachine.dto.ItemDTO;
import com.blackpaw.vendingmachine.model.*;
import com.blackpaw.vendingmachine.service.ItemService;
import com.blackpaw.vendingmachine.service.VendingMachineService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.Availability;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.blackpaw.vendingmachine.fixtures.VendMachineFixture.create_GBVendingMachine_50_Float;
import static com.blackpaw.vendingmachine.model.Coin.FIFTY_PENCE;
import static com.blackpaw.vendingmachine.model.Coin.ONE_POUND;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestCommands.class, VendController.class, VendingMachineService.class})
@ContextConfiguration
class TestCommandsTest {
    @Autowired
    private TestCommands testCommands;
    private ResponseEntity<Object> response;
    private GBVendingMachine vendingMachine = null;

    @MockBean
    private VendController vendController;
    @MockBean
    private ItemService itemService;

    @MockBean
    private VendingMachineService vendingMachineService;

    @BeforeEach
    private void setup() {
        vendingMachine = create_GBVendingMachine_50_Float(VendingMachine.Status.READY);
        Optional<VendingMachine> optionalVendingMachine = Optional.of(vendingMachine);
        response = new ResponseEntity<>(vendingMachine, HttpStatus.OK);

        when(vendingMachineService.getMachine()).thenReturn(optionalVendingMachine);
    }

    @Test
    void listItems(){
        List<ItemDTO> itemDTOList = vendingMachine.getItems().stream().map(item -> {
            return ItemDTO.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .price(item.getPrice()).build();
        }).collect(Collectors.toList());

        when(vendController.listProduct()).thenReturn(new ResponseEntity<>(itemDTOList, HttpStatus.OK));

        String actual = testCommands.list();

        Assertions.assertEquals(
                "ID\t\tNAME \t\t\t\tPrice (£)\r\n" +
                "---------------------------------------------\r\n" +
                "1\t\tSparkling Water\t\t1.75\r\n" +
                "2\t\tEnergy Water\t\t2.5\r\n" +
                "3\t\tRed bull Drink\t\t2.7\r\n" +
                "4\t\tCoke - Original\t\t1.25\r\n" +
                "5\t\tPepsi - Lite\t\t1.25\r\n" +
                "6\t\tTuna Sandwich\t\t3.75\r\n" +
                "7\t\tEgg Sandwich\t\t3.5\r\n" +
                "8\t\tMayo Sandwich\t\t4.5\r\n" +
                "9\t\tSnickers Bar\t\t1.0\r\n" +
                "10\t\tMars Bar    \t\t1.5\r\n" +
                "11\t\tPhone Charger\t\t5.5\r\n" +
                "12\t\tChewing Gum   \t\t0.75\r\n", actual);
    }

    @Test
    void selectItem() {
        ItemDTO energy_water = ItemDTO.builder().id(2)
                .name("Energy Water")
                .price(2.50)
                .build();
        when(vendController.select(2)).thenReturn(new ResponseEntity<>(energy_water, HttpStatus.OK));

        String actual = testCommands.select(2);
        Assertions.assertEquals(
                "Selected item: Energy Water : £2.5\r\n" +
                "please insert coins with 'vend --coins' command\r\n" +
                "\r\n" +
                "0\t\tTWO_POUND\r\n" +
                "1\t\tONE_POUND\r\n" +
                "2\t\tFIFTY_PENCE\r\n" +
                "3\t\tTWENTY_PENCE\r\n" +
                "4\t\tTEN_PENCE\r\n" +
                "5\t\tFIVE_PENCE\r\n" +
                "6\t\tTWO_PENCE\r\n" +
                "7\t\tONE_PENCE\r\n", actual);
    }

    @Test
    void vendItem() {
        VendRequest vendRequest = new VendRequest();

        vendRequest.setItemId(2);
        List<Coin> coins = Arrays.asList(
                ONE_POUND, ONE_POUND, FIFTY_PENCE
        );

        vendRequest.setCoins(coins);
        ResponseEntity<Object> response = new ResponseEntity<>(
                "Item successfully vended. Thank you.\nTake the balance\n" +
                        coins,
                HttpStatus.OK);

        when(vendController.vend(vendRequest)).thenReturn(response);

        String actualResult = testCommands.vend(2, 1, 1, 2);

        Assertions.assertEquals(response.getBody().toString(), actualResult);
    }

    @Test
    void createMachine() {
        when(vendController.create(50)).thenReturn(response);
        testCommands.create(50);
        VendingMachine actualVendingMachine = testCommands.getVendingMachine();
        org.assertj.core.api.Assertions.assertThat(actualVendingMachine)
                .usingRecursiveComparison()
                .isEqualTo(vendingMachine);
    }

    @Test
    void machineAvailabilityCheck_returnUnavailableWhenMachineIsNull() {
        Optional<VendingMachine> optionalVendingMachine = Optional.empty();
        when(vendingMachineService.getMachine()).thenReturn(optionalVendingMachine);

        Availability availability = testCommands.machineAvailabilityCheck();
        Assertions.assertFalse(availability.isAvailable());
        Assertions.assertEquals(availability.getReason(), "Vending Machine has not been created or not ready");
    }


    @Test
    void machineAvailabilityCheck_returnUnavailableWhenMachineNotAvailable() {
        GBVendingMachine vendingMachine = create_GBVendingMachine_50_Float(VendingMachine.Status.NOT_READY);

        testCommands.setVendingMachine(vendingMachine);
        Availability availability = testCommands.machineAvailabilityCheck();
        Assertions.assertFalse(availability.isAvailable());
        Assertions.assertEquals(availability.getReason(), "Vending Machine has not been created or not ready");
    }
}