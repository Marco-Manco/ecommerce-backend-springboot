package com.ecommerce.ecommerce.application.service;

import com.ecommerce.ecommerce.application.dto.CategoriaDTO;
import com.ecommerce.ecommerce.application.mapper.CategoriaMapper;
import com.ecommerce.ecommerce.infrastructure.persistence.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper){
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public List<CategoriaDTO> listarArbol() {
        var raices = categoriaRepository.findByCategoriaPadreId(null);
        return raices.stream()
                .map(categoriaMapper::toDTO)
                .collect(Collectors.toList());
    }
}
