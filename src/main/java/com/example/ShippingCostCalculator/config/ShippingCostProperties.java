package com.example.ShippingCostCalculator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "shipping.cost")
public class ShippingCostProperties {

	private Multiplier multiplier;
	private Threshold threshold;

	@Getter
	@Setter
	public static class Multiplier {
		private float large;
		private float medium;
		private float small;
		private float heavy;
	}

	@Getter
	@Setter
	public static class Threshold {
		private float maxWeightLimit;
		private float heavyParcelWeightLimit;
		private float smallParcelVolumeLimit;
		private float mediumParcelVolumeLimit;
	}
}