package com.mercado.sistema.dao.dto;

public record CategoriaResponse(
    Long id, String nome, String descricao, Integer totalProdutos, Integer totalTerminais) {}
