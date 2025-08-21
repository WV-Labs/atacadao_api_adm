package com.mercado.sistema.dao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoMaisVendido(
    @NotNull Long produtoId,
    @NotBlank String produtoNome,
    @NotBlank String categoriaNome,
    @NotNull @Min(0) Integer quantidadeVendida,
    @NotNull @Min(0) BigDecimal valorTotal,
    @NotNull @Min(0) BigDecimal precoUnitario,
    @NotNull @Min(0) Integer rankingPosicao,
    String codigoBarras) {

  public ProdutoMaisVendido(
      Long produtoId,
      String produtoNome,
      String categoriaNome,
      Integer quantidadeVendida,
      BigDecimal valorTotal) {
    this(
        produtoId,
        produtoNome,
        categoriaNome,
        quantidadeVendida,
        valorTotal,
        BigDecimal.ZERO,
        0,
        null);
  }
}
