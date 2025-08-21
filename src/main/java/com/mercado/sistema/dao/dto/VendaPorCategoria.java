package com.mercado.sistema.dao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record VendaPorCategoria(
    @NotNull Long categoriaId,
    @NotBlank String categoriaNome,
    @NotNull @Min(0) BigDecimal totalVendas,
    @NotNull @Min(0) Integer quantidadeItens,
    @NotNull @Min(0) BigDecimal percentualTotal,
    @NotNull @Min(0) Integer rankingPosicao) {

  public VendaPorCategoria(
      Long categoriaId,
      String categoriaNome,
      BigDecimal totalVendas,
      Integer quantidadeItens) {
    this(categoriaId, categoriaNome, totalVendas, quantidadeItens, BigDecimal.ZERO, 0);
  }
}
