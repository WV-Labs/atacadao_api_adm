package com.mercado.sistema.dao.dto;

public record TerminalResponse(
    Long id,
    String nome,
    String localizacao,
    Integer numero,
    String url,
    Boolean ativo,
    CategoriaResponse categoria,
    Long totalVisualizacoes) {}
