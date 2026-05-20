package com.ecommerce.ecommerce.infrastructure.scheduler;

import com.ecommerce.ecommerce.domain.model.Pedido;
import com.ecommerce.ecommerce.application.service.PedidoService;
import com.ecommerce.ecommerce.infrastructure.persistence.PedidoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservaExpiradaScheduler {

    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;

    public ReservaExpiradaScheduler(PedidoRepository pedidoRepository, PedidoService pedidoService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000) // cada 5 minutos
    @Transactional
    public void liberarReservasExpiradas() {
        List<Pedido> expirados = pedidoRepository.findReservasExpiradas(LocalDateTime.now());
        if (!expirados.isEmpty()) {
            pedidoService.cancelarPedidosExpirados(expirados);
        }
    }
}
