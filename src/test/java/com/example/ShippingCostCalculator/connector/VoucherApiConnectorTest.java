package com.example.ShippingCostCalculator.connector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

import com.example.ShippingCostCalculator.connector.impl.VoucherApiConnectorImpl;
import com.example.ShippingCostCalculator.models.Voucher;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class VoucherApiConnectorTest {

	@InjectMocks
	private VoucherApiConnectorImpl voucherApiConnectorImpl;

	@Mock
	private WebClient webClient;

	@Mock
	private RequestHeadersUriSpec requestHeadersUriSpec;

	@Mock
	private RequestHeadersSpec requestHeadersSpec;

	@Mock
	private WebClient.ResponseSpec responseSpec;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(voucherApiConnectorImpl, "apiEndpoint", "http://mockendpoint/");
		ReflectionTestUtils.setField(voucherApiConnectorImpl, "apiKey", "mockApiKey");
	}

	@Test
	public void testGetVoucherSuccess() {
		String voucherCode = "TEST123";
		Voucher mockVoucher = new Voucher();
		mockVoucher.setCode(voucherCode);
		mockVoucher.setDiscount(10.0f);

		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(Voucher.class)).thenReturn(Mono.just(mockVoucher));

		Voucher result = voucherApiConnectorImpl.getVoucher(voucherCode);

		assertNotNull(result);
		assertEquals(voucherCode, result.getCode());
		assertEquals(10.0, result.getDiscount(), 0.01);
	}

	@Test
	public void testGetVoucherException() {
		String voucherCode = "TEST123";

		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenThrow(new RuntimeException("API failure"));

		Voucher result = voucherApiConnectorImpl.getVoucher(voucherCode);

		assertNull(result);
	}
}
