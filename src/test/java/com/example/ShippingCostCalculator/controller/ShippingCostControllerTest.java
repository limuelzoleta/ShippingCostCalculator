package com.example.ShippingCostCalculator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.ShippingCostCalculator.models.ShippingCostApiRequest;
import com.example.ShippingCostCalculator.models.ShippingCostApiResponse;
import com.example.ShippingCostCalculator.service.ShippingCostService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ShippingCostController.class)
public class ShippingCostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ShippingCostService shippingCostService;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void calculateShippingCost_Success() throws Exception {
		ShippingCostApiResponse response = new ShippingCostApiResponse();
		response.setMessage("Success");

		when(shippingCostService.calculateCost(any(ShippingCostApiRequest.class))).thenReturn(response);

		mockMvc.perform(post("/v1/shipping/calculate-cost").contentType(MediaType.APPLICATION_JSON)
				.content("{\"weight\":10, \"height\":20, \"width\":15, \"length\":25}")).andExpect(status().isOk());
	}

	@Test
	void calculateShippingCost_InvalidParameters() throws Exception {
		mockMvc.perform(post("/v1/shipping/calculate-cost").contentType(MediaType.APPLICATION_JSON)
				.content("{\"weight\":null, \"length\":200, \"width\":50, \"height\":50, \"voucherCode\":\"GGG\"}"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.weight").value("Weight is required"));

		mockMvc.perform(post("/v1/shipping/calculate-cost").contentType(MediaType.APPLICATION_JSON)
				.content("{\"weight\":9, \"length\":null, \"width\":50, \"height\":50, \"voucherCode\":\"GGG\"}"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.length").value("Length is required"));

		mockMvc.perform(post("/v1/shipping/calculate-cost").contentType(MediaType.APPLICATION_JSON)
				.content("{\"weight\":9, \"length\":200, \"width\":null, \"height\":50, \"voucherCode\":\"GGG\"}"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.width").value("Width is required"));

		mockMvc.perform(post("/v1/shipping/calculate-cost").contentType(MediaType.APPLICATION_JSON)
				.content("{\"weight\":9, \"length\":200, \"width\":50, \"height\":null, \"voucherCode\":\"GGG\"}"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.height").value("Height is required"));
	}

	@Test
	void calculateShippingCost_ServerError() throws Exception {
		doThrow(new RuntimeException("Server error")).when(shippingCostService)
				.calculateCost(any(ShippingCostApiRequest.class));

		mockMvc.perform(post("/v1/shipping/calculate-cost").contentType(MediaType.APPLICATION_JSON)
				.content("{\"weight\":10, \"height\":20, \"width\":15, \"length\":25}"))
				.andExpect(status().isInternalServerError());
	}
}
