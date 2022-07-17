package com.blackpaw.vendingmachine.controller;

import com.blackpaw.vendingmachine.dto.ItemDTO;
import com.blackpaw.vendingmachine.model.Item;
import com.blackpaw.vendingmachine.service.ItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(itemDTOs, HttpStatus.OK);
    }

    private ItemDTO convertToDto(Item item) {
        return modelMapper.map(item, ItemDTO.class);
    }
}
