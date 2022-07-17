package com.blackpaw.vendingmachine.service;

import com.blackpaw.vendingmachine.dao.VendingMachineRepository;
import com.blackpaw.vendingmachine.model.Coin;
import com.blackpaw.vendingmachine.model.VendRequest;
import com.blackpaw.vendingmachine.model.VendingMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Map;
import java.util.Optional;

@Service
public class VendingMachineService {

    @Autowired
    private VendingMachineRepository repository;

    public void save(VendingMachine vendingMachine){
        repository.save(vendingMachine);
    }

    public void reset(){
        repository.deleteAll();
    }

    public Optional<VendingMachine> getMachine(){
        return repository.findAll().stream().findFirst();
    }

    public void updateMachine(VendRequest vendRequest, double money){
        VendingMachine machine = getMachine().get();
        Map<Coin, Integer> oldDenominations = machine.getDenominations();

        vendRequest.getCoins().stream().forEach(coin -> {
            oldDenominations.computeIfPresent(coin, (coin1, integer) -> ++integer);
        });
        machine.setCashFloat(machine.getCashFloat() + money);
        repository.save(machine);
    }
}
