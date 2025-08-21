package com.mercado.sistema.dao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ResumoVendas(
    @NotNull @Min(0) BigDecimal totalVendas,
    @NotNull @Min(0) Integer quantidadeVendas,
    @NotNull @Min(0) BigDecimal ticketMedio,
    @NotNull @Min(0) Integer produtosVendidos,
    @NotNull @Min(0) Integer clientesAtendidos,
    @NotNull @Min(0) BigDecimal crescimentoPercentual) {
  public ResumoVendas(BigDecimal totalVendas, Integer quantidadeVendas) {
    this(
        totalVendas,
        quantidadeVendas,
        quantidadeVendas > 0
            ? totalVendas.divide(BigDecimal.valueOf(quantidadeVendas))
            : BigDecimal.ZERO,
        0,
        0,
        BigDecimal.ZERO);
  }
}
