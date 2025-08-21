package com.mercado.sistema.dao.mapper;

import com.mercado.sistema.dao.dto.CategoriaCreateRequest;
import com.mercado.sistema.dao.dto.CategoriaResponse;
import com.mercado.sistema.dao.dto.CategoriaUpdateRequest;
import com.mercado.sistema.dao.model.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        uses = {CategoriaMapper.class})
public interface CategoriaMapper {
  CategoriaMapper INSTANCE = Mappers.getMapper(CategoriaMapper.class);

  // Método customizado para buscar categoria por ID
  @Named("categoriaIdToCategoria")
  default Categoria categoriaIdToCategoria(Long categoriaId) {
    if (categoriaId == null) return null;
    Categoria categoria = new Categoria();
    categoria.setId(categoriaId);
    return categoria;
  }

  // Create Request -> Entity
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "produtos", ignore = true)
  @Mapping(target = "terminais", ignore = true)
  Categoria toEntity(CategoriaCreateRequest request);

  // Update Request -> Entity
  @Mapping(target = "produtos", ignore = true)
  @Mapping(target = "terminais", ignore = true)
  Categoria toEntity(CategoriaUpdateRequest request);

  // Entity -> Response
  @Mapping(
      target = "totalProdutos",
      expression = "java(categoria.getProdutos() != null ? categoria.getProdutos().size() : 0)")
  @Mapping(
      target = "totalTerminais",
      expression = "java(categoria.getTerminais() != null ? categoria.getTerminais().size() : 0)")
  CategoriaResponse toResponse(Categoria categoria);

  // Update Entity com Request
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "produtos", ignore = true)
  @Mapping(target = "terminais", ignore = true)
  void updateEntityFromRequest(CategoriaUpdateRequest request, @MappingTarget Categoria categoria);

}
