package com.mercado.sistema.dao.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record RelatorioVendasResponse(
    @NotNull LocalDate dataInicio,
    @NotNull LocalDate dataFim,
    @NotNull ResumoVendas resumoGeral,
    @NotNull List<VendaPorCategoria> vendasPorCategoria,
    @NotNull List<ProdutoMaisVendido> produtosMaisVendidos,
    @NotNull List<TerminalMaisAcessado> terminaisMaisAcessados,
    @NotNull List<VendaPorDia> vendasPorDia,
    String observacoes) {
  public RelatorioVendasResponse(
          LocalDate dataInicio, LocalDate dataFim, ResumoVendas resumoGeral) {
    this(dataInicio, dataFim, resumoGeral, List.of(), List.of(), List.of(), List.of(), null);
  }
}
