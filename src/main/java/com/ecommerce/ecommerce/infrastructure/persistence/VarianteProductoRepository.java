package com.ecommerce.ecommerce.infrastructure.persistence;

import com.ecommerce.ecommerce.domain.model.VarianteProducto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, Long> {
    List<VarianteProducto> findByProductoId(Long productoId);

    Optional<VarianteProducto> findBySku(String sku);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VarianteProducto v WHERE v.id = :id")
    Optional<VarianteProducto> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT v FROM VarianteProducto v WHERE v.stock - v.stockReservado <= :umbral")
    List<VarianteProducto> findStockBajo(@Param("umbral") int umbral);
}
