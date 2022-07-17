package com.blackpaw.vendingmachine.dao;

import com.blackpaw.vendingmachine.model.VendingMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendingMachineRepository extends JpaRepository<VendingMachine, Long> {
}
