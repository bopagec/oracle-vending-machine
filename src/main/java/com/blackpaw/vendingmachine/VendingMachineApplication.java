package com.blackpaw.vendingmachine;

import com.blackpaw.vendingmachine.dao.ItemRepository;
import com.blackpaw.vendingmachine.model.Item;
import com.blackpaw.vendingmachine.shell.TestCommands;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.Command;

import java.util.List;

@SpringBootApplication
public class VendingMachineApplication{
	private static final Logger logger = LoggerFactory.getLogger(VendingMachineApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(VendingMachineApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
}
