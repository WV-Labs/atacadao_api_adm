package com.mercado.sistema.dao.mapper;

import com.mercado.sistema.dao.dto.ConteudoRequest;
import com.mercado.sistema.dao.model.Conteudo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConteudoMapper {
    @Mapping(target = "id", ignore = true)
    Conteudo toEntity(ConteudoRequest request);
}
