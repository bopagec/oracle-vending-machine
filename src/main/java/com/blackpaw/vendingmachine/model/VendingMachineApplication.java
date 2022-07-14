package com.blackpaw.vendingmachine.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VendingMachineApplication {
	//private ItemRepository itemRepository;

	//@Autowired
	public VendingMachineApplication(ItemRepository itemRepository){
		//this.itemRepository = itemRepository;
	}

	private final Logger logger = LoggerFactory.getLogger(VendingMachineApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(VendingMachineApplication.class, args);
	}


	@Bean
	CommandLineRunner runner(){
		logger.info("loading items...");

		return args -> {
/*
			itemRepository.save(new Item("Water", 1.75, 20));
			itemRepository.save(new Item("Energy Water", 2.50, 20));
			itemRepository.save(new Item("Red bull Drink", 2.70, 20));
			itemRepository.save(new Item("Coke - Original", 1.25, 20));
			itemRepository.save(new Item("Pepsi - Lite", 1.25, 20));
			itemRepository.save(new Item("Tuna Sandwich", 3.75, 20));
			itemRepository.save(new Item("Egg Sandwich", 3.50, 20));
			itemRepository.save(new Item("Beacon & Mayo Sandwich", 4.50, 20));
			itemRepository.save(new Item("Snickers", 1.00, 20));
			itemRepository.save(new Item("Mars", 1.50, 20));
			itemRepository.save(new Item("Car Phone Charger", 5.50, 20));
			itemRepository.save(new Item("Extra White Chewing Gum", 0.75, 20));
*/

			//List<Item> allItems = itemRepository.findAll();
			//logger.info("items loaded: {}", allItems);
		};
	}
}
