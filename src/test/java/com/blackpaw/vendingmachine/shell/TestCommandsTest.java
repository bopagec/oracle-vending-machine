package com.blackpaw.vendingmachine.shell;

import com.blackpaw.vendingmachine.model.GBVendingMachine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
class TestCommandsTest {
    @Autowired
    private TestCommands testCommands;

/*    @Test
    public void testCreateDenomination() {
        double cashFloat = 75;
        GBVendingMachine vendingMachine = new GBVendingMachine();
        vendingMachine.setCashFloat(cashFloat);
        testCommands.createDenominations(vendingMachine);

        double actualInGBP = testCommands.getGbDenominationMap().entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .mapToDouble(value -> (value.getKey().getPence() * value.getValue()) / 100)
                .sum();

        Assertions.assertEquals(cashFloat, actualInGBP);
    }*/
}