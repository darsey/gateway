package com.payment.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class PaymentResponse {

    String transactionId;
    Status status;
    String message;
}
