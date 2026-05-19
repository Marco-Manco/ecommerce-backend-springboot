package com.ecommerce.ecommerce.infrastructure.persistence;

import com.ecommerce.ecommerce.domain.model.Categoria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository {
    List<Categoria> findByActivoTrue();
    List<Categoria> findByCategoriaPadreId(Long padreId);
}
