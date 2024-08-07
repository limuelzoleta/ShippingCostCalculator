package com.example.ShippingCostCalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.example.ShippingCostCalculator.config")
public class ShippingCostCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShippingCostCalculatorApplication.class, args);
	}

}
