package com.ecommerce.ecommerce.infrastructure.persistence.specification;

import com.ecommerce.ecommerce.domain.model.Producto;
import com.ecommerce.ecommerce.domain.model.VarianteProducto;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoSpecification {

    public static Specification<Producto> conFiltros(
            String nombre,
            Long categoriaId,
            BigDecimal precioMin,
            BigDecimal precioMax
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("activo"), true));

            if (nombre != null && !nombre.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("nombre")),
                        "%" + nombre.toLowerCase() + "%"
                ));
            }

            if (categoriaId != null) {
                predicates.add(cb.equal(root.get("categoria").get("id"), categoriaId));
            }

            if (precioMin != null) {
                Subquery<BigDecimal> subMin = query.subquery(BigDecimal.class);
                Root<VarianteProducto> varMin = subMin.from(VarianteProducto.class);
                subMin.select(cb.min(varMin.get("precio")))
                        .where(cb.equal(varMin.get("producto").get("id"), root.get("id")));
                predicates.add(cb.greaterThanOrEqualTo(subMin, precioMin));
            }

            if (precioMax != null) {
                Subquery<BigDecimal> subMax = query.subquery(BigDecimal.class);
                Root<VarianteProducto> varMax = subMax.from(VarianteProducto.class);
                subMax.select(cb.max(varMax.get("precio")))
                        .where(cb.equal(varMax.get("producto").get("id"), root.get("id")));
                predicates.add(cb.lessThanOrEqualTo(subMax, precioMax));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
