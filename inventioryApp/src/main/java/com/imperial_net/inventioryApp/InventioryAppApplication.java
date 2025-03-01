package com.imperial_net.inventioryApp;

import com.imperial_net.inventioryApp.services.UserService;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventioryAppApplication {
	@Autowired
	private  UserService  userService;


	public static void main(String[] args) {
		SpringApplication.run(InventioryAppApplication.class, args);
	}

	@PostConstruct
	public void init() {
		userService.insertAdminUser();
	}
}
