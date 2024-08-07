package com.example.ShippingCostCalculator.models;

import lombok.Data;

@Data
public class ShippingCostApiResponse {
    String message;
    String ruleName;
    float cost;
}
