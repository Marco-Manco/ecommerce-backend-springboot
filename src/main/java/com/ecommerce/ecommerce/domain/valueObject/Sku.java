package com.ecommerce.ecommerce.domain.valueObject;

public record Sku(String value) {
    public Sku {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SKU no puede ser vacío");
        }
        if (!value.matches("^[A-Z0-9-]{3,50}$")) {
            throw new IllegalArgumentException("SKU inválido: " + value);
        }
    }
}
