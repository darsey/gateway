package com.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class PaymentRequest {

    @NotBlank(message = "Card number is required")
    @Size(min = 16, max = 16, message = "Card number must be 16 digits")
    String cardNumber;

    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiry date must be MM/YY")
    String expiryDate;

    @NotBlank(message = "CVV is required")
    @Size(min = 3, max = 3, message = "CVV must be 3 digits")
    String cvv;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    Double amount;

    @NotBlank(message = "Currency is required")
    String currency;

    @NotBlank(message = "Merchant ID is required")
    String merchantId;

    @JsonCreator
    public PaymentRequest(@JsonProperty("cardNumber") String cardNumber,
            @JsonProperty("expiryDate") String expiryDate,
            @JsonProperty("cvv") String cvv,
            @JsonProperty("amount") Double amount,
            @JsonProperty("currency") String currency,
            @JsonProperty("merchantId") String merchantId) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.amount = amount;
        this.currency = currency;
        this.merchantId = merchantId;
    }
}
