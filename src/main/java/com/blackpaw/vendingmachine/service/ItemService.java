package com.blackpaw.vendingmachine.service;

import com.blackpaw.vendingmachine.dao.ItemRepository;
import com.blackpaw.vendingmachine.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private ItemRepository repository;

    @Autowired
    public ItemService(ItemRepository repository){
        this.repository = repository;
    }

    public List<Item> getAll(){
        return repository.findAll();
    }
}
