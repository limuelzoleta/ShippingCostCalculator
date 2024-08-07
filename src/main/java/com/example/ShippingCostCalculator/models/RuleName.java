package com.example.ShippingCostCalculator.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum RuleName {
    REJECT("Reject"),
    HEAVY_PARCEL("Heavy Parcel"),
    SMALL_PARCEL("Small Parcel"),
    MEDIUM_PARCEL("Medium Parcel"),
    LARGE_PARCEL("Large Parcel");

    private final String value;
}

