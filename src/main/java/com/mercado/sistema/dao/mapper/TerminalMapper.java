package com.mercado.sistema.dao.mapper;

import com.mercado.sistema.dao.dto.TerminalCreateRequest;
import com.mercado.sistema.dao.dto.TerminalResponse;
import com.mercado.sistema.dao.dto.TerminalUpdateRequest;
import com.mercado.sistema.dao.model.Categoria;
import com.mercado.sistema.dao.model.Terminal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(
    componentModel = "spring",
    uses = {CategoriaMapper.class})
public interface TerminalMapper {
  TerminalMapper INSTANCE = Mappers.getMapper(TerminalMapper.class);

  // Create Request -> Entity
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "url", ignore = true)
  // URL é gerada automaticamente
  @Mapping(target = "categoria", source = "categoriaId", qualifiedByName = "categoriaIdToCategoria")
  Terminal toEntity(TerminalCreateRequest request);

  // Update Request -> Entity
  @Mapping(target = "url", ignore = true)
  // URL é gerada automaticamente
  @Mapping(target = "categoria", source = "categoriaId", qualifiedByName = "categoriaIdToCategoria")
  Terminal toEntity(TerminalUpdateRequest request);

  // Entity -> Response
  @Mapping(target = "categoria", source = "categoria")
  @Mapping(target = "totalVisualizacoes", ignore = true)
  TerminalResponse toResponse(Terminal terminal);

  // Update Entity com Request
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "url", ignore = true)
  @Mapping(target = "categoria", source = "categoriaId", qualifiedByName = "categoriaIdToCategoria")
  void updateEntityFromRequest(TerminalUpdateRequest request, @MappingTarget Terminal terminal);

  @Named("categoriaIdToCategoria")
  default Categoria categoriaIdToCategoria(Long categoriaId) {
    if (categoriaId == null) return null;
    Categoria categoria = new Categoria();
    categoria.setId(categoriaId);
    return categoria;
  }
}
