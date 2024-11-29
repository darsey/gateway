package com.payment.gateway.model;

public enum Status {

    APPROVED("Approved"),
    DENIED("Denied"),
    PENDING("Pending");


    private final String name;

    Status(String name) {
        this.name = name;
    }

    public static Status forLastDigit(char lastDigit) {
        return Character.getNumericValue(lastDigit) % 2 == 0 ? Status.APPROVED : Status.DENIED;
    }

    public String getName() {
        return name;
    }
}
