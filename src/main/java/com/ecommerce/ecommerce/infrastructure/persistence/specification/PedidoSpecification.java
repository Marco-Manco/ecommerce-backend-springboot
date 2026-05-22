package com.ecommerce.ecommerce.infrastructure.persistence.specification;

import com.ecommerce.ecommerce.domain.model.Pedido;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoSpecification {

    public static Specification<Pedido> conFiltros(
            String search,
            String estado,
            LocalDate desde,
            LocalDate hasta
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("numeroPedido")),
                    "%" + search.toLowerCase() + "%"
                ));
            }

            if (estado != null && !estado.isBlank()) {
                try {
                    var estadoPedido = com.ecommerce.ecommerce.domain.enums.EstadoPedido.valueOf(estado.toUpperCase());
                    predicates.add(cb.equal(root.get("estado"), estadoPedido));
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (desde != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                    root.get("fechaCreacion"), desde.atStartOfDay()
                ));
            }

            if (hasta != null) {
                predicates.add(cb.lessThanOrEqualTo(
                    root.get("fechaCreacion"), hasta.atTime(23, 59, 59)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
