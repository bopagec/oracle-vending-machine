package com.blackpaw.vendingmachine.dao;

import com.blackpaw.vendingmachine.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
