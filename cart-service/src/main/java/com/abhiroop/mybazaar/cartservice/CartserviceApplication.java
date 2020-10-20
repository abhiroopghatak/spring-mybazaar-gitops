package com.abhiroop.mybazaar.cartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
//@EnableHystrixDashboard
//@EnableCircuitBreaker
public class CartserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartserviceApplication.class, args);
		System.out.println("Cart service is booted -up.");
	}
	@Bean
	public RestTemplate getRestTemplate() {
	    return new RestTemplate();
	}
}
