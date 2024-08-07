package com.example.ShippingCostCalculator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ShippingCostCalculator.models.ShippingCostApiRequest;
import com.example.ShippingCostCalculator.models.ShippingCostApiResponse;
import com.example.ShippingCostCalculator.service.ShippingCostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RequestMapping(value = "/v1/shipping")
public class ShippingCostController {
	private final ShippingCostService shippingCostService;

	@PostMapping(value = "/calculate-cost")
	public ResponseEntity<ShippingCostApiResponse> calculateShippingCost(
			@Valid @RequestBody ShippingCostApiRequest request) {
		ShippingCostApiResponse response = new ShippingCostApiResponse();
		try {
			log.info("ShippingCostController: Request received to calculate shipping cost");
			response = shippingCostService.calculateCost(request);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.info("ShippingCostController: Exception encountered while calculating shipping cost: {}",
					e.getMessage());
			response.setMessage("Encountered server error");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
