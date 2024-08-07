package com.example.ShippingCostCalculator.connector.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ShippingCostCalculator.connector.VoucherApiConnector;
import com.example.ShippingCostCalculator.models.Voucher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired()))
@Component
public class VoucherApiConnectorImpl implements VoucherApiConnector {
    WebClient webClient = WebClient.builder().build();
    @Value("${voucher.api.endpoint}")
    private String apiEndpoint;
    @Value("${voucher.api.key}")
    private String apiKey;

    @Override
    public Voucher getVoucher(String code) {
	try {
	    log.info("VoucherApiConnector: getting voucher information for code: {} from {}", code, apiEndpoint);
	    String requestUri = apiEndpoint + code + "?key=" + apiKey;
	    return webClient.get().uri(requestUri).header("Accept", "application/json").retrieve()
		    .bodyToMono(Voucher.class).block();
	} catch (Exception e) {
	    log.info("VoucherApiConnector: unable to get voucher information for code: {}", code);
	    log.info("VoucherApiConnector: Exception encountered while executing api request: {}", e.getMessage());
	}
	return null;
    }
}
