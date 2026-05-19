package com.ecommerce.ecommerce.infrastructure.persistence.specification;

import com.ecommerce.ecommerce.domain.model.Producto;
import jakarta.persistence.criteria.Predicate;
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
    ){
        return(root, query, cb) ->{
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("activo"), true));

            if(nombre != null && !nombre.isBlank()){
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }

            if(categoriaId != null){
                predicates.add(cb.equal(root.get("categoria").get("id"), categoriaId));
            }

            if(precioMin != null){
                predicates.add(cb.greaterThanOrEqualTo(
                        cb.min(root.join("variantes").get("precio")),
                        precioMin
                ));
            }

            if(precioMax != null){
                predicates.add(cb.lessThanOrEqualTo(
                        cb.min(root.join("variantes").get("precio")),
                        precioMax
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
