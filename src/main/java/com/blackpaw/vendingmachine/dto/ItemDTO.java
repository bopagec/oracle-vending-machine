package com.blackpaw.vendingmachine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDTO {
    private String id;
    private String name;
    private double price;
}
