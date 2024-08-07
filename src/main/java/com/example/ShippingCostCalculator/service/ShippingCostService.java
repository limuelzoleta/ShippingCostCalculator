package com.example.ShippingCostCalculator.service;

import com.example.ShippingCostCalculator.models.ShippingCostApiRequest;
import com.example.ShippingCostCalculator.models.ShippingCostApiResponse;

public interface ShippingCostService {
    ShippingCostApiResponse calculateCost(ShippingCostApiRequest request);
}
