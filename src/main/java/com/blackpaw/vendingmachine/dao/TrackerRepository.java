package com.blackpaw.vendingmachine.dao;

import com.blackpaw.vendingmachine.model.Tracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackerRepository extends JpaRepository<Tracker, Long> {
}
