package com.example.ShippingCostCalculator.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShippingCostApiRequest {
	@NotNull(message = "Weight is required")
	Float weight;

	@NotNull(message = "Height is required")
	Float height;

	@NotNull(message = "Width is required")
	Float width;

	@NotNull(message = "Length is required")
	Float length;

	String voucherCode;
}
