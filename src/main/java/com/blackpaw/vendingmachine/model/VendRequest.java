package com.blackpaw.vendingmachine.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@NoArgsConstructor
public class VendRequest {
    private long itemId;
    private List<Coin> coins;
}
