package com.example.ShippingCostCalculator.connector;

import com.example.ShippingCostCalculator.models.Voucher;

public interface VoucherApiConnector {
    Voucher getVoucher(String code);
}
