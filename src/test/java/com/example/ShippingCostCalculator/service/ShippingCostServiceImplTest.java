package com.example.ShippingCostCalculator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ShippingCostCalculator.config.ShippingCostProperties;
import com.example.ShippingCostCalculator.connector.VoucherApiConnector;
import com.example.ShippingCostCalculator.models.RuleName;
import com.example.ShippingCostCalculator.models.ShippingCostApiRequest;
import com.example.ShippingCostCalculator.models.ShippingCostApiResponse;
import com.example.ShippingCostCalculator.models.Voucher;
import com.example.ShippingCostCalculator.service.impl.ShippingCostServiceImpl;

@ExtendWith(MockitoExtension.class)
//@ExtendWith(SpringExtension.class)
class ShippingCostServiceImplTest {
	@Mock
	private VoucherApiConnector voucherApiConnector;

	@InjectMocks
	private ShippingCostServiceImpl shippingCostService;

	@Mock
	private ShippingCostProperties shippingCostProperties;

	@BeforeEach
	public void setUp() {
		when(shippingCostProperties.getMultiplier()).thenReturn(getMultipliers());
		when(shippingCostProperties.getThreshold()).thenReturn(getThreshold());
	}

	@Test
	public void testCalculateCost_HeavyParcel() {
		ShippingCostApiRequest request = new ShippingCostApiRequest();
		request.setWeight(25.0f);

		ShippingCostApiResponse response = shippingCostService.calculateCost(request);

		assertEquals(RuleName.HEAVY_PARCEL.getValue(), response.getRuleName());
		assertEquals(500.0f, response.getCost());

	}

	@Test
	public void testCalculateCost_SmallParcel() {
		ShippingCostApiRequest request = new ShippingCostApiRequest();
		request.setWeight(5.0f);
		request.setLength(10.0f);
		request.setWidth(10.0f);
		request.setHeight(10.0f);

		ShippingCostApiResponse response = shippingCostService.calculateCost(request);

		assertEquals(RuleName.SMALL_PARCEL.getValue(), response.getRuleName());
		assertEquals(30.0f, response.getCost()); // 1000.0 * 0.8
	}

	@Test
	public void testCalculateCost_MediumParcel() {
		ShippingCostApiRequest request = new ShippingCostApiRequest();
		request.setWeight(5.0f);
		request.setLength(20.0f);
		request.setWidth(10.0f);
		request.setHeight(10.0f);

		ShippingCostApiResponse response = shippingCostService.calculateCost(request);

		assertEquals(RuleName.MEDIUM_PARCEL.getValue(), response.getRuleName());
		assertEquals(80.0f, response.getCost()); // 1000.0 * 0.8
	}

	@Test
	public void testCalculateCost_LargeParcel() {
		ShippingCostApiRequest request = new ShippingCostApiRequest();
		request.setWeight(5.0f);
		request.setLength(20.0f);
		request.setWidth(30.0f);
		request.setHeight(10.0f);

		ShippingCostApiResponse response = shippingCostService.calculateCost(request);

		assertEquals(RuleName.LARGE_PARCEL.getValue(), response.getRuleName());
		assertEquals(300.0f, response.getCost());
	}

	@Test
	public void testCalculateCost_WithVoucher() {
		ShippingCostApiRequest request = new ShippingCostApiRequest();
		request.setWeight(5.0f);
		request.setLength(10.0f);
		request.setWidth(10.0f);
		request.setHeight(10.0f);
		request.setVoucherCode("DISCOUNT2024");

		Voucher voucher = new Voucher();
		voucher.setDiscount(5.0f);
		voucher.setExpiry(LocalDate.now().plusDays(1));

		when(voucherApiConnector.getVoucher(anyString())).thenReturn(voucher);

		ShippingCostApiResponse response = shippingCostService.calculateCost(request);

		assertEquals(RuleName.SMALL_PARCEL.getValue(), response.getRuleName());
		assertEquals(25.0f, response.getCost());
	}

	@Test
	public void testCalculateCost_WithVoucher_Empty() {
		ShippingCostApiRequest request = new ShippingCostApiRequest();
		request.setWeight(5.0f);
		request.setLength(10.0f);
		request.setWidth(10.0f);
		request.setHeight(10.0f);
		request.setVoucherCode("");

		ShippingCostApiResponse response = shippingCostService.calculateCost(request);

		assertEquals(RuleName.SMALL_PARCEL.getValue(), response.getRuleName());
		assertEquals(30.0f, response.getCost());
	}

	@Test
	public void testCalculateCost_WithVoucher_Expired() {
		ShippingCostApiRequest request = new ShippingCostApiRequest();
		request.setWeight(5.0f);
		request.setLength(10.0f);
		request.setWidth(10.0f);
		request.setHeight(10.0f);
		request.setVoucherCode("DISCOUNT2024");

		Voucher voucher = new Voucher();
		voucher.setDiscount(5.0f);
		voucher.setExpiry(LocalDate.now().minusDays(1));

		when(voucherApiConnector.getVoucher(anyString())).thenReturn(voucher);

		ShippingCostApiResponse response = shippingCostService.calculateCost(request);

		assertEquals(RuleName.SMALL_PARCEL.getValue(), response.getRuleName());
		assertEquals(30.0f, response.getCost());
	}

	@Test
	public void testCalculateCost_WithVoucher_hasException() {
		ShippingCostApiRequest request = new ShippingCostApiRequest();
		request.setWeight(5.0f);
		request.setLength(10.0f);
		request.setWidth(10.0f);
		request.setHeight(10.0f);
		request.setVoucherCode("DISCOUNT2024");

		when(voucherApiConnector.getVoucher(anyString())).thenReturn(null);

		ShippingCostApiResponse response = shippingCostService.calculateCost(request);

		assertEquals(RuleName.SMALL_PARCEL.getValue(), response.getRuleName());
		assertEquals(30.0f, response.getCost());
	}

	@Test
	public void testCalculateCost_WithVoucher_hasError() {
		ShippingCostApiRequest request = new ShippingCostApiRequest();
		request.setWeight(5.0f);
		request.setLength(10.0f);
		request.setWidth(10.0f);
		request.setHeight(10.0f);
		request.setVoucherCode("DISCOUNT2024");

		Voucher voucher = new Voucher();
		voucher.setError("Error message");

		when(voucherApiConnector.getVoucher(anyString())).thenReturn(voucher);

		ShippingCostApiResponse response = shippingCostService.calculateCost(request);

		assertEquals(RuleName.SMALL_PARCEL.getValue(), response.getRuleName());
		assertEquals(30.0f, response.getCost());
	}

	@Test
	public void testCalculateCost_WithDecimals() {
		ShippingCostApiRequest request = new ShippingCostApiRequest();
		request.setWeight(5.0f);
		request.setLength(10.23f);
		request.setWidth(15.63f);
		request.setHeight(12.05f);

		ShippingCostApiResponse response = shippingCostService.calculateCost(request);

		assertEquals(RuleName.MEDIUM_PARCEL.getValue(), response.getRuleName());
		assertEquals(77.07f, response.getCost());
	}

	@Test
	public void testCalculateCost_ExceedMaxWeight() {
		ShippingCostApiRequest request = new ShippingCostApiRequest();
		request.setWeight(55.0f);

		ShippingCostApiResponse response = shippingCostService.calculateCost(request);

		assertEquals(RuleName.REJECT.getValue(), response.getRuleName());
		assertEquals("Parcel weight exceeded max weight limit", response.getMessage());
	}

	ShippingCostProperties.Multiplier getMultipliers() {
		ShippingCostProperties.Multiplier multiplier = new ShippingCostProperties.Multiplier();
		multiplier.setLarge(0.05f);
		multiplier.setMedium(0.04f);
		multiplier.setSmall(0.03f);
		multiplier.setHeavy(20.0f);

		return multiplier;
	}

	ShippingCostProperties.Threshold getThreshold() {
		ShippingCostProperties.Threshold threshold = new ShippingCostProperties.Threshold();
		threshold.setMaxWeightLimit(50.0f);
		threshold.setHeavyParcelWeightLimit(10.0f);
		threshold.setSmallParcelVolumeLimit(1500.0f);
		threshold.setMediumParcelVolumeLimit(2500.0f);

		return threshold;
	}
}