package com.blackpaw.vendingmachine.service;

import com.blackpaw.vendingmachine.dao.ItemRepository;
import com.blackpaw.vendingmachine.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Item> select(long itemId){
        Optional<Item> item = repository.findById(itemId);

        if(item.isPresent() && item.get().getStock() > 0){
            return item;
        }
        return Optional.empty();
    }
/*
    public VendResponse vend(VendRequest request){
        Optional<Item> item = repository.findById(request.getItemId());

        if(item.isPresent() && item.get().getStock() > 0){
            double price = item.get().getPrice();

            double coinInTotal = request.getCoins().stream().mapToDouble(value -> value.getPence()).sum() / 100;
            if(coinInTotal < price){
                //return VendResponse.builder().status(false).result("not enough money provided, ")
            }

        }

        return null;
    }*/
}
