package com.ecommerce.ecommerce.infrastructure.persistence;

import com.ecommerce.ecommerce.domain.model.Producto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    Page<Producto> findByActivoTrue(Pageable pageable);

    @Query("SELECT p FROM Producto p JOIN FETCH p.variantes JOIN FETCH p.imagenes WHERE p.id = :id")
    Optional<Producto> findByIdWithVariantesAndImagenes(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Producto p JOIN FETCH p.variantes WHERE p.activo = true")
    List<Producto> findAllActiveWithVariantes();
}
