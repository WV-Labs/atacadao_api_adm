package com.mercado.sistema.dao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TerminalMaisAcessado(
    @NotNull Long terminalId,
    @NotBlank String terminalNome,
    @NotBlank String localizacao,
    @NotBlank String categoriaNome,
    @NotNull @Min(0) Long totalVisualizacoes,
    @NotNull @Min(0) Long totalInteracoes,
    @NotNull @Min(0) Double taxaConversao,
    @NotNull @Min(0) Integer rankingPosicao,
    String url) {

  public TerminalMaisAcessado(
      Long terminalId,
      String terminalNome,
      String localizacao,
      String categoriaNome,
      Long totalVisualizacoes) {
    this(
        terminalId, terminalNome, localizacao, categoriaNome, totalVisualizacoes, 0L, 0.0, 0, null);
  }

  public String getTaxaConversaoFormatada() {
    return String.format("%.2f%%", taxaConversao * 100);
  }
}
