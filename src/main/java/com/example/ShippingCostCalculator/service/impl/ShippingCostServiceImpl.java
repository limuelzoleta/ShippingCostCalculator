package com.example.ShippingCostCalculator.service.impl;

import static com.example.ShippingCostCalculator.config.ShippingCostProperties.Multiplier;
import static com.example.ShippingCostCalculator.config.ShippingCostProperties.Threshold;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ShippingCostCalculator.config.ShippingCostProperties;
import com.example.ShippingCostCalculator.connector.VoucherApiConnector;
import com.example.ShippingCostCalculator.models.RuleName;
import com.example.ShippingCostCalculator.models.ShippingCostApiRequest;
import com.example.ShippingCostCalculator.models.ShippingCostApiResponse;
import com.example.ShippingCostCalculator.models.Voucher;
import com.example.ShippingCostCalculator.service.ShippingCostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired()))
@Service
public class ShippingCostServiceImpl implements ShippingCostService {
	private final VoucherApiConnector voucherApiConnector;
	private final ShippingCostProperties shippingCostProperties;

	private final String COST = "cost";
	private final String RULE_NAME = "ruleName";

	@Override
	public ShippingCostApiResponse calculateCost(ShippingCostApiRequest request) {
		ShippingCostApiResponse response = new ShippingCostApiResponse();
		float cost;
		float discount = 0f;

		Multiplier multiplier = shippingCostProperties.getMultiplier();
		Threshold threshold = shippingCostProperties.getThreshold();

		log.info("ShippingCostService: calculateCost service started");
		if (request.getWeight() > threshold.getMaxWeightLimit()) {
			log.info("ShippingCostService: rejected rule applied parcel with weight of {} exceeded limit of {}",
					request.getWeight(), threshold.getMaxWeightLimit());
			return createRejectedResponse();
		}

		if (request.getWeight() > threshold.getHeavyParcelWeightLimit()) {
			log.info("ShippingCostService: heavy rule applied to parcel with weight of {}", request.getWeight());
			response.setRuleName(RuleName.HEAVY_PARCEL.getValue());
			cost = multiplier.getHeavy() * request.getWeight();
		} else {
			Map<String, Object> ruleCostMap = calculateCostFromVolume(request);
			String rule = (String) ruleCostMap.get(RULE_NAME);
			log.info("ShippingCostService: {} rule applied to parcel with weight of {}", rule, request.getWeight());
			response.setRuleName(rule);
			cost = (float) ruleCostMap.get(COST);
		}

		if (request.getVoucherCode() != null && !request.getVoucherCode().isEmpty()) {
			log.info("ShippingCostService: voucher code found! Applying discount");
			discount = getVoucherDiscount(request.getVoucherCode());
		}

		response.setCost(cost - discount);
		return response;
	}

	private ShippingCostApiResponse createRejectedResponse() {
		ShippingCostApiResponse response = new ShippingCostApiResponse();
		response.setRuleName(RuleName.REJECT.getValue());
		response.setMessage("Parcel weight exceeded max weight limit");

		return response;
	}

	private Map<String, Object> calculateCostFromVolume(ShippingCostApiRequest request) {
		Multiplier multiplier = shippingCostProperties.getMultiplier();
		Threshold threshold = shippingCostProperties.getThreshold();
		float cost;

		Map<String, Object> map = new HashMap<>();
		float volume = request.getLength() * request.getWidth() * request.getHeight();
		if (volume < threshold.getSmallParcelVolumeLimit()) {
			map.put(RULE_NAME, RuleName.SMALL_PARCEL.getValue());
			cost = volume * multiplier.getSmall();

		} else if (volume < threshold.getMediumParcelVolumeLimit()) {
			map.put(RULE_NAME, RuleName.MEDIUM_PARCEL.getValue());
			cost = volume * multiplier.getMedium();
		} else {
			map.put(RULE_NAME, RuleName.LARGE_PARCEL.getValue());
			cost = volume * multiplier.getLarge();
		}

		BigDecimal bd = new BigDecimal(Float.toString(cost));
		bd = bd.setScale(2, RoundingMode.HALF_UP);

		map.put(COST, bd.floatValue());

		return map;
	}

	private float getVoucherDiscount(String code) {

		log.info("ShippingCostService - getVoucherDiscount: getting voucher information");

		float discount = 0f;
		Voucher voucher = voucherApiConnector.getVoucher(code);
		if (voucher == null || voucher.getError() != null) {
			log.info("ShippingCostService - getVoucherDiscount: unable to get voucher information");
			return discount;
		}

		if (voucher.getExpiry().isBefore(LocalDate.now())) {
			log.info("ShippingCostService - getVoucherDiscount: voucher is expired");
			return discount;
		}

		log.info("ShippingCostService - getVoucherDiscount: voucher found with discount value of {}",
				voucher.getDiscount());
		return voucher.getDiscount();
	}
}
