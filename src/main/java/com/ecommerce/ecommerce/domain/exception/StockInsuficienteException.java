package com.ecommerce.ecommerce.domain.exception;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String message) {
        super(message);
    }

    public StockInsuficienteException(String sku, int solicitado, int disponible){
        super(String.format(
                "Stock insuficiente para SKU %s: solicitado=%d, disponible=%d",
                sku, solicitado, disponible
        ));
    }
}
