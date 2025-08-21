package com.mercado.sistema.dao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record VendaPorDia(
    @NotNull LocalDate data,
    @NotNull @Min(0) BigDecimal totalVendas,
    @NotNull @Min(0) Integer quantidadeVendas,
    @NotNull @Min(0) BigDecimal ticketMedio,
    @NotNull @Min(0) Integer produtosVendidos) {

  public VendaPorDia(LocalDate data, BigDecimal totalVendas, Integer quantidadeVendas) {
    this(
        data,
        totalVendas,
        quantidadeVendas,
        quantidadeVendas > 0
            ? totalVendas.divide(BigDecimal.valueOf(quantidadeVendas))
            : BigDecimal.ZERO,
        0);
  }
  /** * Retorna a data formatada para exibição */
  public String getDataFormatada() {
    return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
  }
  /** * Retorna o dia da semana */
  public String getDiaSemana() {
    return data.getDayOfWeek().name();
  }
}
