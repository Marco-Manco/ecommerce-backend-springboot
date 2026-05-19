package com.ecommerce.ecommerce.infrastructure.persistence;

import com.ecommerce.ecommerce.domain.model.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByUsuarioId(Long usuarioId);
    List<Direccion> findByUsuarioIdAndEsPrincipalTrue(Long usuarioId);
}
