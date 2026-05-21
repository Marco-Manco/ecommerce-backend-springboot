package com.ecommerce.ecommerce.application.service;

import com.ecommerce.ecommerce.application.dto.ActualizarCategoriaDTO;
import com.ecommerce.ecommerce.application.dto.CategoriaDTO;
import com.ecommerce.ecommerce.application.dto.CrearCategoriaDTO;
import com.ecommerce.ecommerce.application.exception.RecursoNoEncontradoException;
import com.ecommerce.ecommerce.application.mapper.CategoriaMapper;
import com.ecommerce.ecommerce.domain.model.Categoria;
import com.ecommerce.ecommerce.infrastructure.persistence.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminCategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public AdminCategoriaService(CategoriaRepository categoriaRepository,
                                  CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public CategoriaDTO crear(CrearCategoriaDTO request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());

        if (request.categoriaPadreId() != null) {
            Categoria padre = categoriaRepository.findById(request.categoriaPadreId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", request.categoriaPadreId()));
            categoria.setCategoriaPadre(padre);
        }

        return categoriaMapper.toDTO(categoriaRepository.save(categoria));
    }

    public CategoriaDTO actualizar(Long id, ActualizarCategoriaDTO request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", id));

        if (request.nombre() != null) categoria.setNombre(request.nombre());
        if (request.descripcion() != null) categoria.setDescripcion(request.descripcion());
        if (request.activo() != null) categoria.setActivo(request.activo());
        if (request.categoriaPadreId() != null) {
            Categoria padre = categoriaRepository.findById(request.categoriaPadreId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", request.categoriaPadreId()));
            categoria.setCategoriaPadre(padre);
        }

        return categoriaMapper.toDTO(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toDTO)
                .collect(Collectors.toList());
    }
}
