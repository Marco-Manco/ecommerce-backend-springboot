package com.ecommerce.ecommerce.application.port.out;

import com.ecommerce.ecommerce.domain.model.Pedido;

public interface PagoPort {
    String generarLinkPago(Pedido pedido);
    boolean verificarPagoAprobado(Long externalReference);
}
