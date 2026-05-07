package com.navium.andenes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.navium")
public class ApiAndenesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiAndenesApplication.class, args);
	}

}
