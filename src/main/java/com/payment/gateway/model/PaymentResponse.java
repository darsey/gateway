package com.payment.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
public class PaymentResponse {

    String transactionId;
    Status status;
    String message;

    public static class Builder {
        private String transactionId = "";
        private Status status = Status.DENIED;
        private String message = "";

        public Builder withTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder withStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public PaymentResponse build() {
            return new PaymentResponse(transactionId, status, message);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
