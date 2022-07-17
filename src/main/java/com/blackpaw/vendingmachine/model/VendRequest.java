package com.blackpaw.vendingmachine.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class VendRequest {
    private long itemId;
    private List<Coin> coins;
}
