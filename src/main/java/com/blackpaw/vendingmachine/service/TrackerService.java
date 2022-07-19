package com.blackpaw.vendingmachine.service;

import com.blackpaw.vendingmachine.dao.TrackerRepository;
import com.blackpaw.vendingmachine.model.Tracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class TrackerService {
    @Autowired
    private TrackerRepository trackerRepository;

    public void save(Tracker tracker){
        trackerRepository.save(tracker);
    }

    public Map<String, Integer> trackCoins(){
        return trackerRepository.trackCoins();
    }
}
