package com.example.ShippingCostCalculator.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Voucher {
    String code;
    float discount;
    LocalDate expiry;
    String error;
}
