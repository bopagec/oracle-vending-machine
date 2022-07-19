package com.blackpaw.vendingmachine.dao;

import com.blackpaw.vendingmachine.model.Tracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface TrackerRepository extends JpaRepository<Tracker, Long> {
    @Query(
            value = "SELECT SUM(TWO_POUND) as TWO_POUND, sum(ONE_POUND) as ONE_POUND, sum(FIFTY_PENCE) as FIFTY_PENCE, sum(TWENTY_PENCE) as TWENTY_PENCE, sum(TEN_PENCE) as TEN_PENCE, sum(FIVE_PENCE) as FIVE_PENCE, sum(TWO_PENCE) as TWO_PENCE, sum(ONE_PENCE) as ONE_PENCE FROM TRACKER ",
            nativeQuery = true
    )
    Map<String, Integer> trackCoins();
}
