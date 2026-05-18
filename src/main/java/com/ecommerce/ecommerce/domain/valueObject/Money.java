package com.ecommerce.ecommerce.domain.valueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {
    public static Money ARS(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("ARS"));
    }
    public static Money ARS(double amount) {
        return new Money(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP), Currency.getInstance("ARS"));
    }
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("El monto no puede ser nulo");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("No se pueden sumar distintas monedas");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }
}
