package com.ecommerce.ecommerce.application.port.out;

import com.ecommerce.ecommerce.domain.model.Carrito;

public interface GestionarCarritoPort {
    Carrito obtenerCarritoConItems(String email);
    void vaciar(String email);
}
