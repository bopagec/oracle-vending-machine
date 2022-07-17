package com.blackpaw.vendingmachine.shell;

import com.blackpaw.vendingmachine.model.GBVendingMachine;
import com.blackpaw.vendingmachine.model.VendingMachine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class TestCommandsTest {
    private TestCommands testCommands;

    @BeforeEach
    public void setUp(){
        testCommands = new TestCommands();
    }
    @Test
    public void testCreateDenomination(){
        GBVendingMachine vendingMachine = new GBVendingMachine();
        vendingMachine.setCashFloat(25);

        testCommands.createDenominations(vendingMachine);

        double actualInGBP = testCommands.getGbDenominationMap().entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .mapToDouble(value -> (value.getKey().getPence() * value.getValue()) / 100)
                .sum();

        Assertions.assertEquals(25, actualInGBP);
    }
}