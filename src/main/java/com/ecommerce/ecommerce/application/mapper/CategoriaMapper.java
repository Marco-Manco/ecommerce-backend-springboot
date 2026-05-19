package com.ecommerce.ecommerce.application.mapper;

import com.ecommerce.ecommerce.application.dto.CategoriaDTO;
import com.ecommerce.ecommerce.domain.model.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    CategoriaDTO toDTO(Categoria categoria);

    List<CategoriaDTO> toDTOList(List<Categoria> categorias);
}
