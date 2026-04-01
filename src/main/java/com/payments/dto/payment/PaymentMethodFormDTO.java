package com.payments.dto.payment;

public class PaymentMethodFormDTO {
    private String type;
    private String accountId;

    public PaymentMethodFormDTO() {
        // Empty for Spring MVC form binding
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public PaymentMethodDTO toDTO() {
        return switch (type) {
            case "CARD"   -> new CreditCardPaymentDTO(0, type, accountId);
            case "PAYPAL" -> new PaypalPaymentDTO(0, type, accountId);
            case "CRYPTO" -> new CryptoPaymentDTO(0, type, accountId);
            default -> throw new IllegalArgumentException("Unknown payment type: " + type);
        };
    }
}