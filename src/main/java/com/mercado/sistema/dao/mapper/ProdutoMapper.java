package com.mercado.sistema.dao.mapper;

import com.mercado.sistema.dao.dto.ProdutoCreateRequest;
import com.mercado.sistema.dao.dto.ProdutoResponse;
import com.mercado.sistema.dao.dto.ProdutoUpdateRequest;
import com.mercado.sistema.dao.model.Produto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ProdutoMapper.class})
public interface ProdutoMapper {
  ProdutoMapper INSTANCE = Mappers.getMapper(ProdutoMapper.class);

  // Create Request -> Entity
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "categoria", source = "categoriaId", qualifiedByName = "categoriaIdToCategoria")
  Produto toEntity(ProdutoCreateRequest request);

  // Update Request -> Entity (para atualização)
  @Mapping(target = "categoria", source = "categoriaId", qualifiedByName = "categoriaIdToCategoria")
  Produto toEntity(ProdutoUpdateRequest request);

  // Entity -> Response
  ProdutoResponse toResponse(Produto produto);

  // Update Entity com Request (preservando ID e dados não alteráveis)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "categoria", source = "categoriaId", qualifiedByName = "categoriaIdToCategoria")
  void updateEntityFromRequest(ProdutoUpdateRequest request, @MappingTarget Produto produto);

}
