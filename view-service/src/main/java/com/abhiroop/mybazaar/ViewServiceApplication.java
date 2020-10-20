package com.abhiroop.mybazaar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableHystrixDashboard
@EnableCircuitBreaker
public class ViewServiceApplication {

	private static final Logger log = LoggerFactory.getLogger(ViewServiceApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(ViewServiceApplication.class, args);
		log.info("View-Service is ready");
	}

	
	@Bean
	public RestTemplate getRestTemplate() {
	    return new RestTemplate();
	}
	
	
}
