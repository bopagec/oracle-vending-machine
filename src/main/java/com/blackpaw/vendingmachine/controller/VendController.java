package com.blackpaw.vendingmachine.controller;

import com.blackpaw.vendingmachine.dto.ItemDTO;
import com.blackpaw.vendingmachine.model.Item;
import com.blackpaw.vendingmachine.model.VendRequest;
import com.blackpaw.vendingmachine.service.ItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class VendController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ModelMapper modelMapper;

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

    @GetMapping("/vend/{id}")
    public @ResponseBody
    ResponseEntity<Object> vend(@PathVariable long itemId) {
        Optional<Item> vendItem = itemService.vend(itemId);
        return new ResponseEntity<>(vendItem.get(), HttpStatus.OK);
    }

    private ItemDTO convertToDto(Item item) {
        return modelMapper.map(item, ItemDTO.class);
    }
}
